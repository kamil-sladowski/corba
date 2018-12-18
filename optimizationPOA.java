
/**
* optimizationPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from generator.idl
* Monday, November 26, 2018 12:39:32 AM CET
*/

public abstract class optimizationPOA extends org.omg.PortableServer.Servant
 implements optimizationOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable<String, java.lang.Integer> _methods = new java.util.Hashtable<String, Integer> ();
  static
  {
    _methods.put ("register", new java.lang.Integer (0));
    _methods.put ("hello", new java.lang.Integer (1));
    _methods.put ("best_range", new java.lang.Integer (2));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // optimization/register
       {
         short ip = in.read_short ();
         int timeout = in.read_long ();
         org.omg.CORBA.IntHolder id = new org.omg.CORBA.IntHolder ();
         this.register (ip, timeout, id);
         out = $rh.createReply();
         out.write_long (id.value);
         break;
       }

       case 1:  // optimization/hello
       {
         int id = in.read_long ();
         this.hello (id);
         out = $rh.createReply();
         break;
       }

       case 2:  // optimization/best_range
       {
         rangeHolder r = new rangeHolder ();
         this.best_range (r);
         out = $rh.createReply();
         rangeHelper.write (out, r.value);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:optimization:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public optimization _this() 
  {
    return optimizationHelper.narrow(
    super._this_object());
  }

  public optimization _this(org.omg.CORBA.ORB orb) 
  {
    return optimizationHelper.narrow(
    super._this_object(orb));
  }


} // class optimizationPOA
