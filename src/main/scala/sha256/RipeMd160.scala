package sha256

import scala.scalanative.libc.stdlib
import scala.scalanative.libc.string
import scala.scalanative.unsafe._
import scala.scalanative.unsigned._

object RipeMd160 {
  def ripemd160(input: String): Array[UByte] = {
    Zone { implicit z =>
      {
        val str = toCString(input)
        val size: CSize = string.strlen(str)
        val data = alloc[Byte](size).asInstanceOf[Ptr[Byte]]
        string.strncpy(data, str, size)
        ripemd160(data.asInstanceOf[Ptr[UByte]], size)
      }
    }
  }

  def ripemd160(bytearray: Array[UByte]): Array[UByte] = {
    Zone { implicit z =>
      {
        val size = bytearray.size.toLong.toULong
        val data = alloc[UByte](size).asInstanceOf[Ptr[UByte]]
        for (i <- 0 until bytearray.size) {
          !(data + i) = bytearray(i)
        }
        ripemd160(data, size)
      }
    }
  }

  def ripemd160(payload: Ptr[UByte], size: CSize): Array[UByte] = {
    val hash = stdlib.malloc(20L.toULong).asInstanceOf[Ptr[UByte]]
    RipeMd160Extern.ripemd160(hash, payload, size);
    val res = Array.ofDim[UByte](20)
    for (i <- 0 until 20) {
      res(i) = (!(hash + i)).toUByte
    }
    stdlib.free(hash.asInstanceOf[Ptr[Byte]])
    res
  }
}

@extern
object RipeMd160Extern {
  def ripemd160(hash: Ptr[UByte], payload: Ptr[UByte], len: CSize): Unit =
    extern
}