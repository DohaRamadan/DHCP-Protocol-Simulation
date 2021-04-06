import javafx.util.Pair;
import java.io.IOException;
import java.net.*;
import java.sql.Time;
import java.util.*;

public class DHCPServer
{




    public static <bool> void main(String[] args) {
        {
            try {
                DatagramSocket serverSocket;
                serverSocket = new DatagramSocket(5000);
                System.out.println("Server is up");





                byte[] requestBytes = new byte[4096];
                byte[] responseBytes;
                byte[] DHCPrequest = new byte[4096];
                byte [] DHCPacknowledgement ;
                byte [] Lease;
                byte[] macBytes = new byte[20];


                    // making a bool of ip addresses
                    InetAddress serverIP = InetAddress.getByName("localhost"); // 127.0.0.1
                    ArrayList<String> AvailbleIPs = new ArrayList<String>();
                    AvailbleIPs.add("127.0.0.2");
                    AvailbleIPs.add("127.0.0.3");
                    AvailbleIPs.add("127.0.0.4");
                    AvailbleIPs.add("127.0.0.5");
                    AvailbleIPs.add("127.0.0.6");
                    ArrayList<Pair> AssociatedIPs = new ArrayList<Pair>();
                    ArrayList<String> ReservedIPs = new ArrayList<String>();

                    // ip addresses for broadcast, subnetMaskIP,RouterIP and DNS
                    InetAddress broadcast = InetAddress.getByName("255.255.255.255");
                    String subnetMaskIP = "255.255.255.0";
                    String RouterIP = "192.168.1.6";
                    String DNsIP1 = "8.8.8.8";
                    String DNsIP2 = "8.8.4.4";
                    String LeaseTime = "00:02:00";




                while (true) {



                    // Server Status
                    String ServerStatus = "Server status:  " + "Server IP is" + serverIP + "\nAvailable IPS" + AvailbleIPs + "\nReservedIPs" + ReservedIPs
                            + "\nAssociatedIPs" + AssociatedIPs;
                    //System.out.println(ServerStatus);


                    //recive the clientDHCPdiscover packet
                    DatagramPacket clientPacket = new DatagramPacket(requestBytes, requestBytes.length);
                    serverSocket.receive(clientPacket);
                    String clientDHCPdiscover = new String(clientPacket.getData()).trim();


                    // Reciving the MacAddress
                    DatagramPacket MacPacket = new DatagramPacket(macBytes, macBytes.length);
                    serverSocket.receive(MacPacket);
                    String c_mac = new String(MacPacket.getData()).trim();




                    //sending the DHCPOFFERmsg messange
                    int clientPort = clientPacket.getPort();
                    String DHCPOFFERmsg = "Your IP Address can be:" + AvailbleIPs.get(0) + "\nServer IP address is: " + serverIP
                            + "\nRouter IP address: " + RouterIP + "\nSubnet mask" + subnetMaskIP + "\nIP address lease time is: "
                            + LeaseTime + "\nDNS Servers are" + DNsIP1 + ", " + DNsIP2 + "\n\n\n";
                    responseBytes = DHCPOFFERmsg.getBytes();
                    DatagramPacket MyServerPacket = new DatagramPacket(responseBytes, responseBytes.length, broadcast, clientPort);
                    serverSocket.send(MyServerPacket);

                    //pair the ip address and the mac address
                    String OfferedIP = AvailbleIPs.get(0);
                    Pair Offered = new Pair(AvailbleIPs.get(0), c_mac);


                    //recieving the DHCPrequestmsg
                    DatagramPacket clientPacket2 = new DatagramPacket(DHCPrequest, DHCPrequest.length);
                    serverSocket.receive(clientPacket2);
                    String DHCPrequestmsg = new String(clientPacket2.getData()).trim();


                    //sending the lease time
                    String LT = "00:02:00";
                    Lease = LT.getBytes();
                    InetAddress ClientIP = clientPacket2.getAddress();
                    DatagramPacket LeaseTimePacket = new DatagramPacket(Lease, Lease.length, ClientIP, clientPort);
                    serverSocket.send(LeaseTimePacket);


                    AssociatedIPs.add(Offered);
                    ReservedIPs.add(AvailbleIPs.get(0));
                    AvailbleIPs.remove(0);







                    //sending the DHCPacknowledgement
                    String DHCPacknowledgementmsg = "Your IP address is " + AssociatedIPs.get(0) + "\nServer IP is: " + serverIP + "\nRouter IP is "
                            + RouterIP + "\nSubnet mask IP: " + subnetMaskIP + "\nIP address lease time is " + LeaseTime + "\nDNS Servers are: " + DNsIP1
                            + ", " + DNsIP2;
                    DHCPacknowledgement = DHCPacknowledgementmsg.getBytes();
                    DatagramPacket DHCPack = new DatagramPacket(DHCPacknowledgement, DHCPacknowledgement.length, ClientIP, clientPort);
                    serverSocket.send(DHCPack);
                    ServerStatus = "Server Status: " + "Server IP is" + serverIP + "\nAvailable IPS" + AvailbleIPs + "\nReservedIPs" + ReservedIPs
                            + "\nAssociatedIPs" + AssociatedIPs;
                    //System.out.println(ServerStatus);

                    String finalServerStatus = ServerStatus;
                    new Thread (){
                        public void run()
                        {
                            while (true)
                            {
                                Scanner inn = new Scanner(System.in);
                                String commandd = inn.nextLine();
                                if (commandd.equalsIgnoreCase("show status")) {
                                    System.out.print(finalServerStatus);
                                } else {
                                    System.out.print("wrong input");
                                }


                            }
                        }
                    }.start();




                    //time
                    //public void time(ServerStatus){
                    //Boolean LeaseTimeExpired= false;
                    final long ONE_MINUTE_IN_MILLIS=60000;
                    long Lease_time_In_Millis = 60000 ;
                    Calendar AfterAssIP = Calendar.getInstance();
                    long AfterAssIPinMilli = AfterAssIP.getTimeInMillis();
                    Date afterAddingleaseTime=new Date(AfterAssIPinMilli + Lease_time_In_Millis);
                    Thread.sleep(Lease_time_In_Millis);
                    Date dateUpadted = new Date();

                     if(afterAddingleaseTime.getTime()<=dateUpadted.getTime()){

                        AvailbleIPs.add(ReservedIPs.get(0));
                        AssociatedIPs.remove(0);
                        ReservedIPs.remove(0);
                        String CLientIP = "0.0.0.0";
                        String CLientIPStatus = "CLient IP is: " + CLientIP;
                        //System.out.println(CLientIPStatus);
                         System.out.println("\n Time Expired");

                        ServerStatus =  "Server status:  " + "Server IP is" + serverIP + "\nAvailable IPS" + AvailbleIPs + "\nReservedIPs" + ReservedIPs
                                + "\nAssociatedIPs" + AssociatedIPs;

                        //System.out.println(ServerStatus);
                         String ServerStatus2 = ServerStatus;
                         new Thread (){
                             public void run()
                             {
                                 while (true)
                                 {
                                     Scanner inn = new Scanner(System.in);
                                     String commandd = inn.nextLine();
                                     if (commandd.equalsIgnoreCase("show status")) {
                                         System.out.print(ServerStatus2);
                                     } else {
                                         System.out.print("wrong input");
                                     }


                                 }
                             }
                         }.start();




                     }






                    requestBytes = new byte[4096];

                    DHCPrequest = new byte[4096];

                    macBytes = new byte[20];


                }


            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



    }


}