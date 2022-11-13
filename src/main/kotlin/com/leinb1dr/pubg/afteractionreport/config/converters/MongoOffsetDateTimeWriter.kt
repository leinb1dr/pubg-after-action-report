import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import java.time.OffsetDateTime
import java.util.*

@WritingConverter
class MongoOffsetDateTimeWriter : Converter<OffsetDateTime, Date> {

    override fun convert(offsetDateTime: OffsetDateTime): Date? = Date.from(offsetDateTime.toInstant())

}