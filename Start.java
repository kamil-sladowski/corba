
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.omg.CORBA.IntHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.PortableServer.POA;

import com.sun.corba.se.org.omg.CORBA.ORB;

class OptimizationImpl extends optimizationPOA implements optimizationOperations {

    private ConcurrentSkipListMap<Integer, SingleServer> serversID = new ConcurrentSkipListMap<>();
    private ConcurrentSkipListMap<Short, SingleServer> serversIP = new ConcurrentSkipListMap<>();
    private ConcurrentSkipListSet<SingleServer> servers = new ConcurrentSkipListSet<>();
    private int idCount = 0;
    private final String synchronizer = "";

    class SingleServer implements Comparable<SingleServer>{
        private short ip;
        private int id;
        public int timeout;
        private long lastHello;

        public SingleServer(int id, short ip, int timeout) {
            this.id = id;
            this.ip = ip;
            this.timeout = timeout;
            this.lastHello = System.currentTimeMillis();
        }


        public boolean isActive() {
            return System.currentTimeMillis() - lastHello < timeout;
        }

        public void activate() {
            lastHello = System.currentTimeMillis();
        }

        @Override
        public int compareTo(SingleServer o) {
            return this.ip - o.ip;
        }
    }


    @Override
    public void register(short ip, int timeout, IntHolder id) {
        if (! serversIP.containsKey(ip)) {
            synchronized (synchronizer)
            {
                idCount +=1;
                id.value = idCount;
            }
            SingleServer newServer = new SingleServer(id.value, ip, timeout);
            servers.add(newServer);
            serversIP.put(ip, newServer);
            serversID.put(id.value, newServer);
        } else {
            serversIP.get(ip).timeout = timeout;
            id.value = serversIP.get(ip).id;
        }
    }

    @Override
    public void hello(int id) {
        if (serversID.containsKey(id)) {
            serversID.get(id).activate();
        }
    }

    @Override
    public void best_range(rangeHolder r) {
        range finalRange = null;
        range tmpRange = null;
        int tmpSize = 0;
        int finalSize = 0;
        for (SingleServer sItem : servers) {
            if(sItem.isActive()) {
                if (tmpRange == null) {
                    tmpRange = new range(sItem.ip, sItem.ip);
                    tmpSize =0;
                } else {
                    if (sItem.ip - 1 == tmpRange.to) {
                        tmpRange.to += 1;
                        tmpSize +=1;
                    } else {
                        tmpRange = new range(sItem.ip, sItem.ip);
                        tmpSize =0;
                    }
                }
            }

            if (tmpSize > finalSize){
                finalRange = tmpRange;
                finalSize = tmpSize;
                tmpSize = 0;
            }
        }
        r.value = finalRange;
    }
}

class Start {

    public static void main(String[] args) {
        try {
            org.omg.CORBA.ORB orb = ORB.init(args, null);
            POA rootpoa = (POA) orb.resolve_initial_references("RootPOA");
            rootpoa.the_POAManager().activate();

            OptimizationImpl optimizationImpl = new OptimizationImpl();
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(optimizationImpl);

            System.out.println(orb.object_to_string(ref));

            org.omg.CORBA.Object namingContextObj = orb.resolve_initial_references("NameService");
            NamingContext nCont = NamingContextHelper.narrow(namingContextObj);
            NameComponent[] path = { new NameComponent("Optymalizacja", "Object") };

            nCont.rebind(path, ref);
            orb.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
