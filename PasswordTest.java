import org.mindrot.jbcrypt.BCrypt;

public class PasswordTest {
    public static void main(String[] args) {
        String password = "admin123";
        String hashFromDatabase = "$2a$10$7JB720yubVSOfvVWbazBuOWShWvheWjxVYaGYoUaxMNh4qDql5KLO";
        
        System.out.println("=== BCrypt 密码验证测试 ===");
        System.out.println("原始密码: " + password);
        System.out.println("数据库哈希: " + hashFromDatabase);
        
        boolean isMatch = BCrypt.checkpw(password, hashFromDatabase);
        System.out.println("密码匹配结果: " + isMatch);
        
        if (!isMatch) {
            System.out.println("\n❌ 数据库密码哈希错误！");
            System.out.println("=== 生成新的正确哈希 ===");
            String correctHash = BCrypt.hashpw(password, BCrypt.gensalt());
            System.out.println("新生成的哈希: " + correctHash);
            System.out.println("新哈希验证: " + BCrypt.checkpw(password, correctHash));
        } else {
            System.out.println("✅ 密码验证成功！数据库哈希正确。");
        }
        
        // 测试其他可能的密码
        String[] testPasswords = {"admin", "123456", "password"};
        System.out.println("\n=== 测试其他可能的密码 ===");
        for (String testPwd : testPasswords) {
            boolean match = BCrypt.checkpw(testPwd, hashFromDatabase);
            System.out.println("密码 '" + testPwd + "' 匹配结果: " + match);
            if (match) {
                System.out.println("⚠️ 发现匹配的密码: " + testPwd);
            }
        }
    }
}