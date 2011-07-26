package arriba.scala.fix

import arriba.fix.Tags
import java.util.{Arrays}
import collection.mutable.LinkedList
import java.io.{IOException, ByteArrayOutputStream}

object FixFieldCollection {

  class Builder {
    def this() {
      this ()
    }

    def addField(tag: Int, value: String): FixFieldCollection.Builder = {
      //TODO need to make this list stuff work
      //fields ++= new Field[String](tag, value)
      this
    }

    def build: FixFieldCollection = {
      new FixFieldCollection(this.fields)
    }

    private var fields: LinkedList[Field[String]] = new LinkedList[Field[String]]
  }

}

class FixFieldCollection {
  private def this(fields: List[Field[String]]) {
    this ()
    val sortedFields = fields.sortBy(f => f.tag)
    this.tagArray = new Array[Int](sortedFields.size)
    this.valueArray = new Array[String](sortedFields.size)
    var arrayIndex: Int = 0
    for (field <- sortedFields) {
      this.tagArray(arrayIndex) = field.tag
      this.valueArray(arrayIndex) = field.value
      ({
        arrayIndex += 1; arrayIndex - 1
      })
    }
  }

  def toByteArray: Array[Byte] = {
    try {
      var tag: Int = 0
      var value: String = null
      val messageBytes: ByteArrayOutputStream = new ByteArrayOutputStream
      {
        var tagIndex: Int = 0
        while (tagIndex < tagArray.length) {
          {
            tag = tagArray(tagIndex)
            value = valueArray(tagIndex)
            val tagBytes: Array[Byte] = Tags.toByteArray(tag)
            val valueBytes: Array[Byte] = value.getBytes
            messageBytes.write(tagBytes)
            messageBytes.write(Fields.EQUAL_SIGN)
            messageBytes.write(valueBytes)
          }
          ({
            tagIndex += 1; tagIndex
          })
        }
      }
      messageBytes.write(Fields.DELIMITER)
      messageBytes.toByteArray
    }
    catch {
      case e: IOException => {
        new Array[Byte](0)
      }
    }
  }

  def getValue(tag: Int): String = {
    val valueIndex: Int = Arrays.binarySearch(this.tagArray, tag)
    if (valueIndex < 0) "" else this.valueArray(valueIndex)
  }

  private var tagArray: Array[Int] = null
  private var valueArray: Array[String] = null
}

