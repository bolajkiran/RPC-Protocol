import java.io.IOException;

public class RPCServer {
    public static void main(String[] args) {
        System.out.println(" _________________                _________________");
        System.out.println("|   ___________   |              |   ___________   |");
        System.out.println("|  |           |  |              |  |           |  |");
        System.out.println("|  |  SERVER   |  |     RPC      |  |  CLIENT   |  |");
        System.out.println("|  |___________|  |  --------->  |  |___________|  | ");
        System.out.println("|_________________|  <---------  |_________________|");
        System.out.println("      _|____|_ . . . . . . . . . . . . _|____|_      ");
        System.out.println("     /________\\                       /________\\    ");
        new Thread(() -> {
            try {
                IServer serviceServer = new RPCServerStub(8088);
                serviceServer.register(IComputationService.class, ComputationServiceImpl.class);
                serviceServer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

