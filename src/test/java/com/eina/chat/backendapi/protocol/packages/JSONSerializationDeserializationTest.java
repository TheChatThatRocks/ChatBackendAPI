package com.eina.chat.backendapi.protocol.packages;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
public class JSONSerializationDeserializationTest {

    @Autowired
    private JacksonTester<SendCommandPackage> sendCommandPackageJacksonTester;

    @Test
    void testSerialize() throws Exception {
        SendCommandPackage sendCommandPackage =  new SendCommandPackage(TypeOfMessage.ADD_ACCOUNT,
                4, new AddAccountArgument("testUser", "testPassword"));

        String obtainedJSON = this.sendCommandPackageJacksonTester.write(sendCommandPackage).getJson();

        assert (obtainedJSON.equals("{\"typeOfMessage\":\"ADD_ACCOUNT\",\"messageId\":4,\"argument\":{\"username\":\"testUser\",\"password\":\"testPassword\"}}"));
    }

    @Test
    void testDeserialize() throws Exception {
        String contentToDeserialize = "{\"typeOfMessage\":\"ADD_ACCOUNT\",\"messageId\":4,\"argument\":{\"username\":\"testUser\",\"password\":\"testPassword\"}}";
        // {"typeOfMessage":"ADD_ACCOUNT","messageId":4,"argument":{"username":"testUser","password":"testPassword"}}
//        String content = "{\"make\":\"Ford\",\"model\":\"Focus\"}";
//        assertThat(this.json.parse(content))
//                .isEqualTo(new VehicleDetails("Ford", "Focus"));
//        assertThat(this.json.parseObject(content).getMake()).isEqualTo("Ford");
    }
}
