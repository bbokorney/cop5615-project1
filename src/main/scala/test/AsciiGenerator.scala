/**
 * AsciiGenerator is an object that takes in an Int and produces a string based
 * off of the Ascii representation of the string. AsciiGenerator can then make
 * the next value of the string with the nextString method which also modifies
 * the internal string. The current string can be obtained by the getString 
 * method and the internal string can be reset to the initial string with the
 * Init method.
 * 
 * The default values are:
 * initial: 0
 * asciiStart: 33
 * asciiEnd: 176
 * 
 * AsciiGenerator simply takes a range of ascii values (asciiStart and asciiEnd)
 * and creates the string (based on initial) that represents the range of ascii
 * values. Essentially, it treats initial as a base asciiEnd-aciiStart value
 */
package test

class AsciiGenerator(initial:Int = 0, asciiStart:Int = 33, asciiEnd:Int = 126)
{
	private val base = asciiEnd - asciiStart + 1
	private var buff = new StringBuffer()
	private var counter:Int = 0;
	makeAscii() /* Initialize */
	
	/**
	 * Creates an Ascii representation of initial.
	 * @return	returns the Ascii representation of initial
	 */
	private def makeAscii(): String =
	{
	  var temp = initial
	  var i = 0
	  var length = 0;
	  
	  /* Re-make buff */
	  buff = new StringBuffer()
	  
	  /* Find length of string */
	  if(temp > 0) length = (Math.log(temp)/Math.log(base)).toInt
	  
	  /* Build characters in reverse order */
	  for(i <- 0 to length)
	  {
	    buff.append(((temp % base) + asciiStart).asInstanceOf[Char])
	    temp = temp/base - 1
	  }
	  
	  return buff.reverse().toString()
	}
	
	/**
	 * Moves on to the next Ascii character by incrementing counter by one and
	 * finding the Ascii representation of initial+coutner. This method 
	 * attempts to change as little as possible in buff instead of simply 
	 * re-computing the entire string. This method treats buff as a counter and
	 * thus stops once the next characters fails to "carry over" 
	 * 
	 * @return	returns the next Ascii string
	 */
	def nextString():String = 
	{
	  buff.reverse() /* put buffer back in "reverse" order */
	  counter += 1
	  var temp = initial + counter
	  
	  /* Note, the first position always changes */
	  buff.setCharAt(0, ((temp % base) + asciiStart).asInstanceOf[Char])
	  temp = temp/base - 1
	  
	  /* Check all the indices that changed values */
	  var i = 1
	  while(temp >= 0 && i < buff.length()) 
	  {
	    buff.setCharAt(i, ((temp % base) + asciiStart).asInstanceOf[Char])
	    i += 1
	    temp = temp/base - 1
	  }
	  
	  /* Fix the last possible change (i is less than buff.length). If this 
	   * happens, it must be the case that there is a new character and that
	   * character must be asciiStart. Remember to fix i (which was incremented
	   * in the last pass) */
	  if(temp == 0 && i-1 < buff.length()) buff.append(((temp % base) + asciiStart).asInstanceOf[Char])
	  
	  return buff.reverse().toString()
	}
	
	/**
	 * Returns the string representation of buff
	 * @return	returns the string representation of the current string
	 */
	def getString():String = buff.toString()
}