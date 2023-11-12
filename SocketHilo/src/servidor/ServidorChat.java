package servidor;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.DefaultListModel;
/**
 *
 * @author RPVZ
 */
public class ServidorChat{
    private DefaultListModel mensajes = new DefaultListModel();
    private static long clienteCounter = 0;
    private static List<Long> clientesActivos = Collections.synchronizedList(new ArrayList<Long>());
    private static AtomicLong nextId = new AtomicLong(1);

    public static void main(String[] args){
        new ServidorChat();
    }
    public ServidorChat(){
        try{
            ServerSocket socketServidor = new ServerSocket(5000);
            while (true){
                Socket cliente = socketServidor.accept();
                Runnable nuevoCliente = new HiloDeCliente(mensajes, cliente, getNextClienteId());
                Thread hilo = new Thread(nuevoCliente);
                hilo.start();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public synchronized static long getNextClienteId() {
        return ++clienteCounter;
    }
    public static void agregarCliente(Long id) {
        clientesActivos.add(id);
    }

    public static void removerCliente(Long id) {
        clientesActivos.remove(id);
    }
    public static long generarNuevoId() {
        return nextId.getAndIncrement();
    }
    public static List<Long> getClientesActivos() {
        return new ArrayList<>(clientesActivos);
    }
    
    
}
