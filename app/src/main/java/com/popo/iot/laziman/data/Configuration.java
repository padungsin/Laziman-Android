package com.popo.iot.laziman.data;

import com.popo.laziman.cloud.iot.CloudConfig;

public class Configuration {

    private long id;
    public static String projectId;
    public static String cloudRegion;
    public static String registryId;
    public static String mqttBridgeHost;
    public static int mqttBridgePort;
    public static String serviceAccount;
    public static String privateKeyFile;
    public static String algorithm;
    public static String googleAPICredential;
    public static String myId;


    public CloudConfig createCloudConfig(){
        CloudConfig cloudConfig = new CloudConfig();
        cloudConfig.projectId = this.projectId;
        cloudConfig.cloudRegion = this.cloudRegion;
        cloudConfig.registryId = this.registryId;
        cloudConfig.mqttBridgeHost = this.mqttBridgeHost;
        cloudConfig.mqttBridgePort = this.mqttBridgePort;
        cloudConfig.serviceAccount = this.serviceAccount;
        cloudConfig.privateKeyFile = this.privateKeyFile;
        cloudConfig.algorithm = this.algorithm;


        return cloudConfig;
    }

    public long getId() {
        return id;
    }

    public static String getProjectId() {
        return projectId;
    }

    public static String getCloudRegion() {
        return cloudRegion;
    }

    public static String getRegistryId() {
        return registryId;
    }

    public static String getMqttBridgeHost() {
        return mqttBridgeHost;
    }

    public static int getMqttBridgePort() {
        return mqttBridgePort;
    }

    public static String getServiceAccount() {
        return serviceAccount;
    }

    public static String getPrivateKeyFile() {
        return privateKeyFile;
    }

    public static String getAlgorithm() {
        return algorithm;
    }


    public static String getGoogleAPICredential() {
        return googleAPICredential;
    }

    public static String getMyId() {
        return myId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static void setProjectId(String projectId) {
        Configuration.projectId = projectId;
    }

    public static void setCloudRegion(String cloudRegion) {
        Configuration.cloudRegion = cloudRegion;
    }

    public static void setRegistryId(String registryId) {
        Configuration.registryId = registryId;
    }


    public static void setMqttBridgeHost(String mqttBridgeHost) {
        Configuration.mqttBridgeHost = mqttBridgeHost;
    }

    public static void setMqttBridgePort(int mqttBridgePort) {
        Configuration.mqttBridgePort = mqttBridgePort;
    }

    public static void setServiceAccount(String serviceAccount) {
        Configuration.serviceAccount = serviceAccount;
    }

    public static void setPrivateKeyFile(String privateKeyFile) {
        Configuration.privateKeyFile = privateKeyFile;
    }

    public static void setAlgorithm(String algorithm) {
        Configuration.algorithm = algorithm;
    }


    public static void setGoogleAPICredential(String googleAPICredential) {
        Configuration.googleAPICredential = googleAPICredential;
    }

    public static void setMyId(String myId) {
        Configuration.myId = myId;
    }

    @Override
    public String toString() {
        return getProjectId() + "/" + getCloudRegion() + "/" + getRegistryId();
    }
}
