package com.itheima;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "DB_URL=jdbc:mysql://localhost:3306/tlias",
        "DB_USERNAME=root",
        "DB_PASSWORD=test-only",
        "JWT_SIGN_KEY=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=",
        "OSS_ENDPOINT=https://oss-cn-beijing.aliyuncs.com",
        "OSS_BUCKET_NAME=test-bucket",
        "OSS_REGION=cn-beijing"
})
class TliasWebManagementApplicationTests {

    @Test
    void contextLoads() {
    }

}
