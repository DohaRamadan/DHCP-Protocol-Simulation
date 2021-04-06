import java.io.IOException;
import java.net.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class DHCPClient  {
    public static String randomMACAddress() {
        Random rand = new Random();
        byte[] macAddr = new byte[6];
        rand.nextBytes(macAddr);

        macAddr[0] = (byte) (macAddr[0] & (byte) 254);  //zeroing last 2 bytes to make it unicast and locally adminstrated

        StringBuilder sb = new StringBuilder(18);
        for (byte b : macAddr) {

            if (sb.length() > 0)
                sb.append(":");

            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress serverIP = InetAddress.getByName("localhost");
            int serverPort = 5000;
            byte[] requestBytes;
            byte[] responseFromServer = new byte[4096];
            byte[] DHCPREQUESTmsg;
            byte[] DHCPacknowledgement = new byte[4096];
            byte[] LT = new byte[4096];
            String mac = randomMACAddress();
            byte[] macBytes;
            macBytes = mac.getBytes();


                // sending the server the DHCPdiscover message
                String DHCPDISCOVER = "DHCPDISCOVER:source MAC address is : " + mac + ", destination MAC address is : 255.255.255.255  ";
                requestBytes = DHCPDISCOVER.getBytes();
                InetAddress broadcast = InetAddress.getByName("255.255.255.255");
                DatagramPacket myClientPacket = new DatagramPacket(requestBytes, requestBytes.length, broadcast, serverPort);
                clientSocket.send(myClientPacket);


                // sending the mac address
                DatagramPacket MacPacket = new DatagramPacket(macBytes, macBytes.length, broadcast, serverPort);
                clientSocket.send(MacPacket);

                //recieving the DHCPoffermsg
                DatagramPacket serverPacket = new DatagramPacket(responseFromServer, responseFromServer.length);
                clientSocket.receive(serverPacket);
                String DHCPoffermsg = new String(serverPacket.getData()).trim();


                //sending the DHCPREQUESTmsg
                String DHCPrequest = "I request IP address offered" + "DHCP server IP" + serverIP;
                DHCPREQUESTmsg = DHCPrequest.getBytes();
                DatagramPacket myClientPacket2 = new DatagramPacket(DHCPREQUESTmsg, DHCPREQUESTmsg.length, serverIP, serverPort);
                clientSocket.send(myClientPacket2);

            // Reciving the Lease time
            DatagramPacket LeaseTimemsg = new DatagramPacket(LT, LT.length);
            clientSocket.receive(LeaseTimemsg);
            String LeaseTime = new String(LeaseTimemsg.getData()).trim();


                //Reciving the DHCPacknowledgementmsg
                DatagramPacket DHCPacknowledgementmsg = new DatagramPacket(responseFromServer, responseFromServer.length);
                clientSocket.receive(DHCPacknowledgementmsg);
                String ClientStatus;
                ClientStatus = new String(serverPacket.getData()).trim();
                System.out.println("Client Status: " + ClientStatus);





            //time
            final long ONE_MINUTE_IN_MILLIS=60000;
            final long Lease_time_In_Millis =60000 ;
            Calendar AfterAssIP = Calendar.getInstance();
            long AfterAssIPinMilli = AfterAssIP.getTimeInMillis();
            Date afterAddingleaseTime=new Date(AfterAssIPinMilli + Lease_time_In_Millis);
            Thread.sleep(Lease_time_In_Millis);
            Date dateUpadted = new Date();

            if (afterAddingleaseTime.getTime()<=dateUpadted.getTime()) {
                String ClientIP = "0.0.0.0";
                ClientStatus = "Client Status: Your IP address is " + ClientIP + "\nMac Address: " + mac;
                System.out.println("\nTime Expired");
            }

            responseFromServer = new byte[4096];

            DHCPacknowledgement = new byte[4096];

            } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}