package com.nanabell.sponge.nico

import org.slf4j.Logger
import org.slf4j.Marker

class TopicLogger(private val wrapped: Logger, vararg topics: String) : Logger {

    private val topic = topics.joinToString(" ", "[", "]") + " "

    override fun getName(): String = wrapped.name + topic

    override fun isWarnEnabled(): Boolean = wrapped.isWarnEnabled
    override fun isWarnEnabled(marker: Marker?): Boolean = wrapped.isWarnEnabled(marker)
    override fun warn(msg: String?) = wrapped.warn(topic + msg)
    override fun warn(format: String?, arg: Any?) = wrapped.warn(topic + format, arg)
    override fun warn(format: String?, vararg arguments: Any?) = wrapped.warn(topic + format, arguments)
    override fun warn(format: String?, arg1: Any?, arg2: Any?) = wrapped.warn(topic + format, arg1, arg2)
    override fun warn(msg: String?, t: Throwable?) = wrapped.warn(topic + msg, t)
    override fun warn(marker: Marker?, msg: String?) = wrapped.warn(marker, topic + msg)
    override fun warn(marker: Marker?, format: String?, arg: Any?) = wrapped.warn(marker, topic + format, arg)
    override fun warn(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = wrapped.warn(marker, topic + format, arg1, arg2)
    override fun warn(marker: Marker?, format: String?, vararg arguments: Any?) = wrapped.warn(marker, topic + format, arguments)
    override fun warn(marker: Marker?, msg: String?, t: Throwable?) = wrapped.warn(marker, topic + msg, t)

    override fun isInfoEnabled(): Boolean = wrapped.isInfoEnabled
    override fun isInfoEnabled(marker: Marker?): Boolean = wrapped.isInfoEnabled(marker)
    override fun info(msg: String?) = wrapped.info(topic + msg)
    override fun info(format: String?, arg: Any?) = wrapped.info(topic + format, arg)
    override fun info(format: String?, arg1: Any?, arg2: Any?) = wrapped.info(topic + format, arg1, arg2)
    override fun info(format: String?, vararg arguments: Any?) = wrapped.info(topic + format, arguments)
    override fun info(msg: String?, t: Throwable?) = wrapped.info(topic + msg, t)
    override fun info(marker: Marker?, msg: String?) = wrapped.info(marker, topic + msg)
    override fun info(marker: Marker?, format: String?, arg: Any?) = wrapped.info(marker, topic + format, arg)
    override fun info(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = wrapped.info(marker, topic + format, arg1, arg2)
    override fun info(marker: Marker?, format: String?, vararg arguments: Any?) = wrapped.info(marker, topic + format, arguments)
    override fun info(marker: Marker?, msg: String?, t: Throwable?) = wrapped.info(marker, topic + msg, t)

    override fun isErrorEnabled(): Boolean = wrapped.isErrorEnabled
    override fun isErrorEnabled(marker: Marker?): Boolean = wrapped.isErrorEnabled(marker)
    override fun error(msg: String?) = wrapped.error(topic + msg)
    override fun error(format: String?, arg: Any?) = wrapped.error(topic + format, arg)
    override fun error(format: String?, arg1: Any?, arg2: Any?) = wrapped.error(topic + format, arg1, arg2)
    override fun error(format: String?, vararg arguments: Any?) = wrapped.error(topic + format, arguments)
    override fun error(msg: String?, t: Throwable?) = wrapped.error(topic + msg, t)
    override fun error(marker: Marker?, msg: String?) = wrapped.error(marker, topic + msg)
    override fun error(marker: Marker?, format: String?, arg: Any?) = wrapped.error(marker, topic + format, arg)
    override fun error(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = wrapped.error(marker, topic + format, arg1, arg2)
    override fun error(marker: Marker?, format: String?, vararg arguments: Any?) = wrapped.error(marker, topic + format, arguments)
    override fun error(marker: Marker?, msg: String?, t: Throwable?) = wrapped.error(marker, topic + msg, t)

    override fun isDebugEnabled(): Boolean = wrapped.isDebugEnabled
    override fun isDebugEnabled(marker: Marker?): Boolean = wrapped.isDebugEnabled(marker)
    override fun debug(msg: String?) = wrapped.debug(topic + msg)
    override fun debug(format: String?, arg: Any?) = wrapped.debug(topic + format, arg)
    override fun debug(format: String?, arg1: Any?, arg2: Any?) = wrapped.debug(topic + format, arg1, arg2)
    override fun debug(format: String?, vararg arguments: Any?) = wrapped.debug(topic + format, arguments)
    override fun debug(msg: String?, t: Throwable?) = wrapped.debug(topic + msg, t)
    override fun debug(marker: Marker?, msg: String?) = wrapped.debug(marker, topic + msg)
    override fun debug(marker: Marker?, format: String?, arg: Any?) = wrapped.debug(marker, topic + format, arg)
    override fun debug(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = wrapped.debug(marker, topic + format, arg1, arg2)
    override fun debug(marker: Marker?, format: String?, vararg arguments: Any?) = wrapped.debug(marker, topic + format, arguments)
    override fun debug(marker: Marker?, msg: String?, t: Throwable?) = wrapped.debug(marker, topic + msg, t)

    override fun isTraceEnabled(): Boolean = wrapped.isTraceEnabled
    override fun isTraceEnabled(marker: Marker?): Boolean = wrapped.isTraceEnabled(marker)
    override fun trace(msg: String?) = wrapped.debug(topic + msg)
    override fun trace(format: String?, arg: Any?) = wrapped.debug(topic + format, arg)
    override fun trace(format: String?, arg1: Any?, arg2: Any?) = wrapped.debug(topic + format, arg1, arg2)
    override fun trace(format: String?, vararg arguments: Any?) = wrapped.debug(topic + format, arguments)
    override fun trace(msg: String?, t: Throwable?) = wrapped.debug(topic + msg, t)
    override fun trace(marker: Marker?, msg: String?) = wrapped.debug(marker, topic + msg)
    override fun trace(marker: Marker?, format: String?, arg: Any?) = wrapped.debug(marker, topic + format, arg)
    override fun trace(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = wrapped.debug(marker, topic + format, arg1, arg2)
    override fun trace(marker: Marker?, format: String?, vararg argArray: Any?) = wrapped.debug(marker, topic + format, argArray)
    override fun trace(marker: Marker?, msg: String?, t: Throwable?) = wrapped.debug(marker, topic + msg, t)

}
