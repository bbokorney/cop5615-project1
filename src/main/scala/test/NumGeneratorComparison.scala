package test

object NumGeneratorComparison extends App {

  val startVal = 0
  val lower = 10000000
  val step = 10000000
  val upper = 1000000000

  println("NumValues\tASCII\tLong")

  for(endVal <- Range(lower, upper, step)) {
    compareForRange(startVal, endVal)
  }


  def compareForRange(startVal: Int, endVal: Int) = {
    var startTime: Long = 0
    var endTime: Long = 0
    val asciiGen = new AsciiGenerator(initial = startVal)
    val intGen = new LongGenerator

    startTime = System.currentTimeMillis()
    for(i <- startVal until endVal) {
      val temp = asciiGen.nextString()
    }
    endTime = System.currentTimeMillis()
    val asciiDur = endTime - startTime

    startTime = System.currentTimeMillis()
    for(i <- startVal until endVal) {
      val temp = intGen.encode(i)
    }
    endTime = System.currentTimeMillis()
    val longDur =  endTime - startTime

    println("%s\t%s\t%s".format(endVal-startVal, asciiDur, longDur))
  }
}

