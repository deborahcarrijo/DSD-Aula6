import java.io.*;
import java.net.*;
import java.util.*;

public class BankServer {
    private static final String ARQUIVO_CONTAS = "contas.txt";
    private static Map<String, Double> contas = new HashMap<>();

    public static void main(String[] args) {
        try {
            // Carregar contas do arquivo
            carregarContas();
            System.out.println("Servidor iniciado. Aguardando conexões...");

            ServerSocket servidorSocket = new ServerSocket(12345);
            while (true) {
                Socket clienteSocket = servidorSocket.accept();
                System.out.println("Cliente conectado.");

                // Thread para tratar cada cliente
                new Thread(() -> tratarCliente(clienteSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void tratarCliente(Socket clienteSocket) {
        try (BufferedReader entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
                PrintWriter saida = new PrintWriter(clienteSocket.getOutputStream(), true)) {

            // Receber solicitação do cliente
            String solicitacao = entrada.readLine();
            System.out.println("Solicitação recebida: " + solicitacao);

            String[] partes = solicitacao.split(",");
            String numeroConta = partes[0];
            String operacao = partes[1];
            double valor = Double.parseDouble(partes[2]);

            // Processar solicitação
            String resposta = processarSolicitacao(numeroConta, operacao, valor);
            saida.println(resposta);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized String processarSolicitacao(String numeroConta, String operacao, double valor) {
        if (!contas.containsKey(numeroConta)) {
            return "Erro: Conta não encontrada.";
        }

        double saldo = contas.get(numeroConta);
        switch (operacao.toLowerCase()) {
            case "deposito":
                saldo += valor;
                contas.put(numeroConta, saldo);
                salvarContas();
                return "Depósito realizado com sucesso. Saldo atualizado: R$ " + saldo;
            case "saque":
                if (saldo >= valor) {
                    saldo -= valor;
                    contas.put(numeroConta, saldo);
                    salvarContas();
                    return "Saque realizado com sucesso. Saldo atualizado: R$ " + saldo;
                } else {
                    return "Erro: Saldo insuficiente.";
                }
            default:
                return "Erro: Operação inválida.";
        }
    }

    private static void carregarContas() throws IOException {
        try (BufferedReader leitor = new BufferedReader(new FileReader(ARQUIVO_CONTAS))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                String[] partes = linha.split(",");
                String numeroConta = partes[0];
                double saldo = Double.parseDouble(partes[1]);
                contas.put(numeroConta, saldo);
            }
        }
    }

    private static void salvarContas() {
        try (PrintWriter escritor = new PrintWriter(new FileWriter(ARQUIVO_CONTAS))) {
            for (Map.Entry<String, Double> entrada : contas.entrySet()) {
                escritor.println(entrada.getKey() + "," + entrada.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}