package com.recursivity.commons.bean

import collection.mutable.MutableList
import collection.immutable.LinearSeq
import collection.TraversableLike
import java.lang.reflect.{TypeVariable, ParameterizedType}

/**
 * Created by IntelliJ IDEA.
 * User: wfaler
 * Date: Nov 29, 2010
 * Time: 11:48:44 PM
 * To change this template use File | Settings | File Templates.
 */

object BeanUtils {
  def instantiate(cls: Class[_]): Any = {
    val cons = cls.getConstructors.head
    val list = new MutableList[AnyRef]
    cons.getParameterTypes.foreach(cls => {
      cls.getName match {
        case "long" => {
          val l: Long = 0
          list += l.asInstanceOf[AnyRef]
        }
        case "int" => {
          val l: Int = 0
          list += l.asInstanceOf[AnyRef]
        }
        case "float" => {
          val f = 0.0f
          list += f.asInstanceOf[AnyRef]
        }
        case "double" => {
          val f = 0.0d
          list += f.asInstanceOf[AnyRef]
        }
        case "boolean" => {
          val b = false
          list += b.asInstanceOf[AnyRef]
        }
        case "short" => {
          val l: Short = 0
          list += l.asInstanceOf[AnyRef]
        }
        case "byte" => {
          val b = new java.lang.Byte("0")
          list += b.asInstanceOf[AnyRef]
        }
        case "char" => {
          val c = new java.lang.Character('c')
          list += c
        }
        case "scala.Option" => list += None
        case _ => list += null

      }
    })
    return cons.newInstance(list.toArray: _*)
  }

  def setProperty(cls: Class[_], bean: Any, key: String, value: Any) {
    try {
      val field = cls.getDeclaredField(key)
      val fieldCls = getClassForJavaPrimitive(field.getType)
      field.setAccessible(true)
      if(classOf[ParameterizedType].isAssignableFrom(field.getGenericType.getClass)){
        val parameterized = field.getGenericType.asInstanceOf[ParameterizedType]
        field.set(bean, resolveGenerifiedValue(fieldCls, GenericsParser.parseDefinition(parameterized), value))
      }else{
        val transformer = TransformerRegistry.resolveTransformer(fieldCls)
        field.set(bean, transformer.toValue(value.toString))
      }
    } catch {
      case e: NoSuchFieldException => {
        if (cls.getSuperclass != null)
          setProperty(cls.getSuperclass, bean, key, value)
      }
    }
  }

  def resolveGenerifiedValue(cls: Class[_], genericType: GenericTypeDefinition, input: Any): Any = {

    if (classOf[TraversableLike[_ <: Any, _ <: Any]].isAssignableFrom(cls)) { // temporary workaround, collection types not yet supported
      val c = Class.forName(genericType.genericTypes.get.head.clazz)
      val transformer = TransformerRegistry.resolveTransformer(c)
      if(cls.equals(classOf[List[_]]) || cls.equals(classOf[Array[_]]) || cls.equals(classOf[Set[_]])){
        val list = new MutableList[Any]
        if(input.isInstanceOf[List[_]]){
          val l = input.asInstanceOf[List[_]]
          l.foreach(f => list += transformer.toValue(f.toString))
        }else if(input.isInstanceOf[Array[_]]){
          val array = input.asInstanceOf[Array[_]]
          array.foreach(f => list += transformer.toValue(f.toString))
        }
        if(cls.equals(classOf[List[_]]))
          return list.toList
        else if(cls.equals(classOf[Set[_]]))
          return list.toSet
        else if(cls.equals(cls.equals(classOf[Array[_]])))
          return list.toArray
      }
      return null
    } else if (classOf[java.util.Collection[_ <: Any]].isAssignableFrom(cls)) {
      return null
    } else if (classOf[Option[_ <: Any]].isAssignableFrom(cls)) {
      val c = Class.forName(genericType.genericTypes.get.head.clazz)
      if(genericType.genericTypes.get.head.genericTypes.equals(None)){
        val transformer = TransformerRegistry.resolveTransformer(c)
        return Some(transformer.toValue(input.toString))
      }else{
        val t = genericType.genericTypes.get.head
        val targetCls = Class.forName(t.clazz)
        return Some(resolveGenerifiedValue(targetCls, t, input))
      }
    } else {
      return null
    }
  }

  def instantiate(cls: Class[_], properties: Map[String, Any]): Any = {
    val bean = instantiate(cls)

    properties.keys.foreach(key => {
      setProperty(cls, bean, key, properties(key))
    })

    return bean
  }


  private def getClassForJavaPrimitive(cls: Class[_]): Class[_] = {

    var fieldCls: Class[_] = null
    cls.getName match {
      case "long" => fieldCls = classOf[Long]
      case "int" => fieldCls = classOf[java.lang.Integer]
      case "float" => fieldCls = classOf[java.lang.Float]
      case "double" => fieldCls = classOf[java.lang.Double]
      case "boolean" => fieldCls = classOf[Boolean]
      case "short" => fieldCls = classOf[java.lang.Short]
      case _ => fieldCls = cls
    }
    return fieldCls
  }
}