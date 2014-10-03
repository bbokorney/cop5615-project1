package test

import java.math.BigInteger

import org.apache.commons.codec.binary.Base64

class LongGenerator {
  def encode(num: Long): String = {
    b64EncodeInt(num)
  }

  def b64EncodeInt(num: Long): String = {
    new String(Base64.encodeInteger(BigInteger.valueOf(num)))
  }
}
