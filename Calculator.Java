/**
 * File: Calculator.java
 *
 * Description: Implements a multifunction integer calculator.
 * Here's how it works. All calculations are performed using
 * two internal registers, the accumulator, which keeps a running
 * total of ongoing calculations, and the displayRegister, which
 * is set to the value displayed in the external display just
 * before an operation is performed. Inputs to the calculator
 * occur by pressing either digit keys, which signal that an
 * integer operand is being input, and operation keys (+ - * / C =),
 * which indicate that an operation should be performed.
 *
 * Control of the machine is managed by the handleKeyPress()
 * method. Two internal state variables are used to control
 * the machine.
 *   (1) The displayState variable keeps track of when
 *       a new number is being started (REPLACE) and when incoming digits
 *       should be APPENDed to the present value of the display. In APPEND
 *       mode, it appends each digit typed to the external display. In
 *       REPLACE mode, it replaces the external display with the digit
 *       typed.
 *   (2) The opCode variable stores the current operation.
 *       What makes control of the calculator somewhat tricky is that it
 *       uses INFIX notation, so operations take the form:
 *       Operand1 opCode Operand2
 *       The OpCode has to be remembered while Operand2 is being input.
 *       The following example shows the state of these control variables
 *       after each key press. The vertical lines (|) show when each
 *       operation is done. For the expression 56 + 23 - 5 the calculator
 *       does the addition (+) after the - key is pressed. It does the
 *       subtraction (-) after the = key is pressed. The equal key resets
 *       everything but the accumulator, which always stores the present
 *       total. The accumulator is only reset after a Clear or an error.
 *
 *       KEYPRESS: 5 6 + | 2 3 - | 5 = |
 *       DISPLAY: 0 5 56 56 56 2 23 23 79 5 74
 *       DISPLAYREG: 0 56 0 23 0 5 0
 *       ACCUM: 0 0 56 56 56 79 79 74
 *       OPCODE -1 1 1 2 -1
 *       DISPLAYSTATE: R A R A R A R
 *
 *
 */
 
/** 
 * 
 * @author Aziz Ahmed
 * 
 */

public class Calculator {
  public final static int NOOP = -1;  // Operations
  public final static int EQUAL = 0;
  public final static int ADD = 1;
  public final static int SUBTRACT = 2;
  public final static int MULTIPLY = 3;
  public final static int DIVIDE = 4;
  public final static int CLEAR = 5;
  public final static int APPEND = 0; // Display states
  public final static int REPLACE = 1;
  public final static int STORE = 7;
  public final static int RECALL = 8;
  public final static int CLEAR_MEMORY = 0;
  public final static int SQUARE = 6;
  public static String saveStr = ""; // store result to display with RM
  
  private int accumulator; // The actual accumulator
  // Internal memory for the visible textField
  private int displayRegister;
  // The operation that's waiting for Operand2
  private int opCode;
  // State of the external display (REPLACE or APPEND)
  private int displayState;
  // Set true if divide-by-zero error
  private boolean error;

  /**
   * Calculator() constructor initializes the machine.
   */
  public Calculator () {
    initialize();
  } // End constructor

  /**
   * initialize() resets the two registers and the opCode
   * and displayState variables. This is invoked initially
   * and then after each clear (C) and after each Error.
   */
  private void initialize()  {
    // Reset the machine's registers
    accumulator = 0;
    displayRegister = 0;
    // Replace the display with the next digit typed
    displayState = REPLACE;
    opCode = NOOP; // No current operation
    error = false;
  } // End initialize()
 /**
  * check if the button is a number
  * to use different color for numeric buttons
  * 
  * @param str
  * @return true/false
  */
  public static boolean isNumeric(String str) { 
	  try {  
	    Double.parseDouble(str);  
	    return true;
	  } catch(NumberFormatException e){  
	    return false;  
	  }  
	}
  /**
   * check if string is a punctuation 
   * 
   * @param str
   * @return true/false
   */
  public static boolean isSymbol(String str) {
	  return str.matches("[\\p{Punct}&&[^@',&]]");
	}
  
  /**
   * keyToIntCode() converts key characters, such as '+',
   * to an integer code.
   * @param keyCH -- key character string
   * @return an int representing the key that was pressed or an error code
   */
  private int keyToIntCode(char keyCh) {

    switch (keyCh) {
      case 'C': return CLEAR;
      case '=': return EQUAL;
      case '+': return ADD;
      case '-': return SUBTRACT;
      case '*': return MULTIPLY;
      case '/': return DIVIDE;
      case 'S': return STORE;
      case 'R': return RECALL;
      case 'M': return CLEAR_MEMORY;
      case 'X': return SQUARE;
    }
    return -1;      // Error Return
  } // End keyToIntCode()

  /**
   * doCurrentOp() is invoked whenever C, =, +, -, *, or /
   * is pressed.  Except  for the Clear key, it performs
   * an operation, storing the result in the accumulator,
   * which is returned as the method's value. The Clear key
   * is treated as a special case and the machine is
   * reinitialized. For all other keys the opCode field
   * stores the operation that is waiting for its second
   * operand to be input. That operation is performed,
   * using the accumulator and displayRegister. After each
   * operation, the displayState is set to REPLACE, so the
   * next digit typed will start a new integer operand.
   * Note that EQUAL key, resets the opCode  to NOOP, thus
   * beginning another complete INFIX operation. All other
   * keys (+ - / *) set the opCode to their keyCodes, thus
   * getting the machine ready to complete that operation
   * the next time an operator key is pressed.
   * @param keyCode -- an int representing one of EQUAL, ADD,
   *    SUBTRACT, DIVIDE, MULT, CLEAR
   * @return an int representing the result of the calculation
   */
  private int doCurrentOp(int keyCode) { 
    // do current opCode
    if (keyCode == CLEAR) {
      initialize();
      return accumulator;
    }
    switch (opCode) {
      case NOOP: 
        accumulator = displayRegister; break;
      case ADD : 
        accumulator = accumulator + displayRegister; break;
      case SUBTRACT : 
        accumulator = accumulator - displayRegister; break;
      case MULTIPLY : 
        accumulator = accumulator * displayRegister; break;
      case DIVIDE :
        if (displayRegister == 0) {
          error = true;
        } else {
          accumulator = accumulator/displayRegister;
        }
      break;
      case STORE :
    	  accumulator = displayRegister; break;
      case RECALL :
    	  accumulator = displayRegister; break;
      case SQUARE : 
    	  accumulator = accumulator * accumulator; break; 
    } // End switch
    if (keyCode == EQUAL) {
      opCode = NOOP;  // Reset opCode
    } else {
      opCode = keyCode; // Set up for next operation
    }
    displayState = REPLACE;
    displayRegister = 0;
    return accumulator;
  } // End doCurrentOp

  /**
   * handleKeyPress() handles all key presses. It
   * distinguishes two types of keys, digit keys, which
   * are used to create integer operands  by appending digits,
   * and opCode keys such as '+' and 'C'.
   * @param keyStr -- key pressed
   * @param external_display -- result to be displayed
   * @return a String giving the value that should be displayed
   *     by the calculator.
   */
  public String handleKeyPress(String keyStr, String external_display) {
    String resultStr = ""; // Stores the result
    int Mem;
    // add the Store Memory function 

     if (keyStr.equals("SM")) { 
    	 Mem = Integer.parseInt(external_display);
    	 resultStr = Integer.toString(Mem); // Return result
    	 saveStr = resultStr;
    	 return resultStr;
     } 
     if (keyStr.equals("RM")) { 
    	 resultStr = saveStr;
    	 return resultStr;
     } 
     if (keyStr.equals("CM")) { 
    	 saveStr = "";
    	 return resultStr;
     }
     if (keyStr.equals("X^2")) { 
    	 Mem = Integer.parseInt(external_display);
    	 resultStr = Integer.toString(Mem * Mem); // Return result
    	 return resultStr;
     } 
    char keyCh = keyStr.charAt(0); // Convert the key label to a char
    if (Character.isDigit(keyCh)) { // If this was a digit key
      if (displayState == APPEND) { //  either append it
        resultStr = external_display + keyCh;
      } else { //  or use it to replace the display
        displayState = APPEND;
        resultStr = keyStr;
      }
    } else { // If not a digit key, it must be an opCode
      // Get display value
      displayRegister = Integer.parseInt(external_display);
      // Perform an operation
      int result = doCurrentOp(keyToIntCode(keyCh));
      if (!error) {
        resultStr = Integer.toString(result); // Return result
      } else {
        resultStr = "Error";
        initialize();
      }
    }
    return resultStr;  // return the result
  } // End andleKeyPress()
} // End Calculator Class