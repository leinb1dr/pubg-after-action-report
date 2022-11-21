package com.leinb1dr.pubg.afteractionreport.config.converters

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

@ReadingConverter
class MongoOffsetDateTimeReader : Converter<Date, OffsetDateTime> {

    override fun convert(date: Date): OffsetDateTime = date.toInstant().atOffset(ZoneOffset.UTC)
}