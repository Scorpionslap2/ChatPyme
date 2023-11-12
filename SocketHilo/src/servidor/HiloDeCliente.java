package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
/**
 *
 * @author RPVZ
 */
public class HiloDeCliente implements Runnable, ListDataListener{
    private DefaultListModel mensajes;
    private Socket socket;
    private DataInputStream dataInput;
    private DataOutputStream dataOutput;
    private long clienteId;


    public HiloDeCliente(DefaultListModel mensajes, Socket socket, long clienteId){
        this.mensajes = mensajes;
        this.socket = socket;
        this.clienteId = ServidorChat.generarNuevoId();
        ServidorChat.agregarCliente(clienteId);
        try {
            dataInput = new DataInputStream(socket.getInputStream());
            dataOutput = new DataOutputStream(socket.getOutputStream());
            mensajes.addListDataListener(this);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            while (true) {
                String texto = dataInput.readUTF();
    
                // Si el cliente envía una solicitud de "LISTAR_USUARIOS"
                if ("LISTAR_USUARIOS".equals(texto)) {
                    // Aquí, obtendrías la lista de todos los clientes conectados.
                    // Suponiendo que hay una función llamada `getClientesActivos` en `ServidorChat` que te da esta lista:
                    List<Long> usuarios = ServidorChat.getClientesActivos();
    
                    StringBuilder sb = new StringBuilder("Usuarios conectados: ");
                    for (Long id : usuarios) {
                        sb.append(id).append(", ");
                    }
    
                    // Envía esta lista de vuelta al cliente que hizo la solicitud.
                    dataOutput.writeUTF(sb.toString());
                } else {
                    // El manejo normal de los mensajes.
                    synchronized (mensajes) {
                        // En lugar de imprimir directamente, añadir el identificador del cliente al mensaje
                        String mensajeCompleto = "Cliente " + Thread.currentThread().getId() + ": " + texto;
                        mensajes.addElement(mensajeCompleto);
                        System.out.println(mensajeCompleto);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            ServidorChat.removerCliente(clienteId);
        }
    }
    

    @Override
    public void intervalAdded(ListDataEvent e){
        String texto = (String) mensajes.getElementAt(e.getIndex0());
        try{
            dataOutput.writeUTF(texto);
        } catch (Exception excepcion){
            excepcion.printStackTrace();
        }
    }
    

    @Override
    public void intervalRemoved(ListDataEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
