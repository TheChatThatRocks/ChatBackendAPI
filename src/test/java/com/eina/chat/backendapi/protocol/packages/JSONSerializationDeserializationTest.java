package com.eina.chat.backendapi.protocol.packages;

import com.eina.chat.backendapi.protocol.packages.signup.request.AddAccountCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
public class JSONSerializationDeserializationTest {

    @Autowired
    private JacksonTester<BasicPackage> sendCommandPackageJacksonTester;

    @Test
    void testSerialize() throws Exception {
        AddAccountCommand addAccountCommand = new AddAccountCommand(4, "testUser", "testPassword");

        String obtainedJSON = this.sendCommandPackageJacksonTester.write(addAccountCommand).getJson();

        System.out.println(obtainedJSON);
    }

    @Test
    void testDeserialize() throws Exception {
        String contentToDeserialize = "{\"typeOfMessage\":\"ADD_ACCOUNT\",\"messageId\":4,\"username\":\"testUser\",\"password\":\"testPassword\"}";
        BasicPackage sendCommandPackage = sendCommandPackageJacksonTester.parseObject(contentToDeserialize);


        assert (sendCommandPackage instanceof AddAccountCommand);
        AddAccountCommand addAccountCommand = (AddAccountCommand) sendCommandPackage;
        assert (addAccountCommand.getUsername().equals("testUser"));
        assert (addAccountCommand.getPassword().equals("testPassword"));
    }
}
