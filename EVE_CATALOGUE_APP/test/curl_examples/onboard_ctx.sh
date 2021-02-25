curl -X POST http://localhost:8082/ctx/catalogue/ctxblueprint \
  -b pippo_token \
  -H 'Content-Type: application/json' \
  -d '{
    "ctxBlueprint": {
        "version": "0.1",
        "name": "trafficGenerator",
        "description": "Traffic Generator Service",
        "parameters": [
            {
                "parameterId": "flow_amount",
                "parameterName": "flow_amount",
                "parameterType": "number",
                "parameterDescription": "number of flows",
                "applicabilityField": "test"
            }
        ],
        "atomicComponents": [
            {
                "componentId": "source",
                "serversNumber": 3,
                "endPointsIds": [
                    "source_data_ext"
                ]
            },
            {
                "componentId": "EPC",
                "serversNumber": 1,
                "endPointsIds": [
                    "pEPC_data"
                ]
            }
        ],
        "endPoints": [
            {
                "endPointId": "tg_sap_data",
                "external": true,
                "management": true,
                "ranConnection": true
            },
            {
                "endPointId": "source_data_ext",
                "external": true,
                "management": true,
                "ranConnection": false
            }
        ],
        "connectivityServices": [
            {
                "endPointIds": [
                    "tg_sap_data",
                    "source_data_ext"
                ],
                "external": true
            }
        ],
	"applicationMetrics": [ {
    		"metricId": "metricSample",
    		"name": "metricSampleName",
    		"metricCollectionType": "DELTA",
    		"unit": "s",
    		"interval": "5s",
    		"topic": "metricSampleTopic"
    	}]
    },
    "nsds": [
        {
            "version": "0.1",
            "nsdIdentifier": "trafficGenerator",
            "designer": "NXW",
            "nsdName": "Traffic Generator Service",
            "nsdInvariantId": "Traffic Generator Service",
            "vnfdId": [
                "source"
            ],
            "sapd": [
                {
                    "cpdId": "tg_sap_data",
                    "description": "SAP for all the TG service",
                    "layerProtocol": "IPV4",
                    "cpRole": "ROOT",
                    "addressData": [
                        {
                            "addressType": "IP_ADDRESS",
                            "iPAddressAssignment": false,
                            "floatingIpActivated": true,
                            "iPAddressType": "IPv4",
                            "numberOfIpAddress": 1
                        }
                    ],
                    "sapAddressAssignment": false,
                    "nsVirtualLinkDescId": "tg_vl_data"
                }
            ],
            "virtualLinkDesc": [
                {
                    "virtualLinkDescId": "tg_vl_data",
                    "virtualLinkDescProvider": "NXW",
                    "virtuaLinkDescVersion": "0.1",
                    "connectivityType": {
                        "layerProtocol": "IPV4"
                    },
                    "virtualLinkDf": [
                        {
                            "flavourId": "fc_vl_df_data",
                            "qos": {
                                "latency": 0,
                                "packetDelayVariation": 0,
                                "packetLossRatio": 0,
                                "priority": 0
                            },
                            "serviceAvaibilityLevel": "LEVEL_1",
                            "bitrateRequirements": {
                                "root": 2,
                                "leaf": 2
                            }
                        }
                    ],
                    "description": "Network to connect the TG to the external NWs"
                }
            ],
            "nsDf": [
                {
                    "nsDfId": "nsTg_df",
                    "flavourKey": "nsTg_fk",
                    "vnfProfile": [
                        {
                            "vnfProfileId": "source_profile",
                            "vnfdId": "source",
                            "flavourId": "source_df",
                            "instantiationLevel": "source_il",
                            "minNumberOfInstances": 1,
                            "maxNumberOfInstances": 3,
                            "nsVirtualLinkConnectivity": [
                                {
                                    "virtualLinkProfileId": "tg_vl_profile_data",
                                    "cpdId": [
                                        "tg_data_ext"
                                    ]
                                }
                            ]
                        }
                    ],
                    "virtualLinkProfile": [
                        {
                            "virtualLinkProfileId": "tg_vl_profile_data",
                            "virtualLinkDescId": "tg_vl_data",
                            "flavourId": "tg_vl_df_data",
                            "maxBitrateRequirements": {
                                "root": "1",
                                "leaf": "1"
                            },
                            "minBitrateRequirements": {
                                "root": "1",
                                "leaf": "1"
                            }
                        }
                    ],
                    "nsInstantiationLevel": [
                        {
                            "nsLevelId": "nsTg_il",
                            "description": "Default instantiation level for the TG service",
                            "vnfToLevelMapping": [
                                {
                                    "vnfProfileId": "source_profile",
                                    "numberOfInstances": 1
                                }
                            ],
                            "virtualLinkToLevelMapping": [
                                {
                                    "virtualLinkProfileId": "tg_vl_profile_data",
                                    "bitRateRequirements": {
                                        "root": "1",
                                        "leaf": "1"
                                    }
                                }
                            ]
                        }
                    ],
                    "defaultNsInstantiationLevelId": "nsTg_il",
                    "dependencies": []
                }
            ],
            "security": {
                "signature": "NSD_SIGNATURE",
                "algorithm": "NSD_ALGORITHM",
                "certificate": "NSD_CERTIFICATE"
            }
        }
    ],
    "translationRules": [
        {
            "nsdId": "trafficGenerator",
            "nsdVersion": "0.1",
            "nsFlavourId": "nsTg_df",
            "nsInstantiationLevelId": "nsTg_il",
            "input": [
                {
                    "parameterId": "flow_amount",
                    "minValue": "1",
                    "maxValue": "5"
                }
            ]
        }
    ]
}'
