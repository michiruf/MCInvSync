//package mrnavastar.invsync.data;
//
//import org.hibernate.annotations.GenericGenerator;
//
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;
//import javax.persistence.Table;
//
//
///**
// * @author Michael Ruf
// * @since 2023-01-04
// */
//@Entity
//@Table(name = "server")
//public class ServerData {
//
//    @Id
//    //@GeneratedValue
//    //@UuidGenerator
//    @GeneratedValue(generator = "UUID")
//    @GenericGenerator(
//            name = "UUID",
//            strategy = "org.hibernate.id.UUIDGenerator"
//    )
//    public String serverId;
//    public String serverName;
//
//    public boolean isInitialized;
//
//    protected ServerData() {
//    }
//
//    public ServerData(String serverId) {
//        this.serverId = serverId;
//    }
//}
