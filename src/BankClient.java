import java.io.*;
import java.net.*;

public class BankClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
                BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Cliente conectado ao servidor.");

            System.out.print("Digite o número da conta: ");
            String numeroConta = console.readLine();

            System.out.print("Digite a operação (deposito/saque): ");
            String operacao = console.readLine();

            System.out.print("Digite o valor: ");
            double valor = Double.parseDouble(console.readLine());

            // Enviar solicitação ao servidor
            String solicitacao = numeroConta + "," + operacao + "," + valor;
            saida.println(solicitacao);

            // Receber resposta do servidor
            String resposta = entrada.readLine();
            System.out.println("Resposta do servidor: " + resposta);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
