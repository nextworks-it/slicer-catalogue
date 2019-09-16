curl -X POST http://localhost:8082/vs/catalogue/vsblueprint \
  -b pippo_token \
  -H 'Content-Type: application/json' \
  -d '{
    "vsBlueprint": {
        "version": "0.1",
        "name": "AstiAGVControl",
        "description": "Asti AGV control service ",
        "parameters": [
            {
                "parameterId": "sensor_amount",
                "parameterName": "sensor_amount",
                "parameterType": "number",
                "parameterDescription": "number of guiding sensors",
                "applicabilityField": "efactory"
            },
            {
                "parameterId": "vehicle_amount",
                "parameterName": "vehicle_amount",
                "parameterType": "number",
                "parameterDescription": "number of autonomous guided vehicles",
                "applicabilityField": "efactory"
            }
        ],
        "atomicComponents": [
            {
                "componentId": "masterPLC",
                "serversNumber": 3,
                "endPointsIds": [
                    "mplc_data_ext"
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
                "endPointId": "agv_sap_data",
                "external": true,
                "management": true,
                "ranConnection": true
            },
            {
                "endPointId": "mplc_data_ext",
                "external": true,
                "management": true,
                "ranConnection": false
            },
            {
                "endPointId": "pEPC_data",
                "external": true,
                "management": true,
                "ranConnection": false
            }
        ],
        "connectivityServices": [
            {
                "endPointIds": [
                    "agv_sap_data",
                    "mplc_data_ext",
                    "pEPC_data"
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
            "nsdIdentifier": "nsAstiAgvControl",
            "designer": "NXW",
            "version": "0.1",
            "nsdName": "Autonomous Guided Vehicle Control service @ 5GEVE",
            "nsdInvariantId": "Autonomous Guided Vehicle Control service @ 5GEVE",
            "vnfdId": [
                "masterPLC"
            ],
            "pnfdId": [
                "EPC"
            ],
            "sapd": [
                {
                    "cpdId": "agv_sap_data",
                    "description": "SAP for all the AGV service",
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
                    "nsVirtualLinkDescId": "agv_vl_data"
                }
            ],
            "virtualLinkDesc": [
                {
                    "virtualLinkDescId": "agv_vl_data",
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
                    "description": "Network to connect to the AGV devices"
                }
            ],
            "nsDf": [
                {
                    "nsDfId": "nsAgv_df",
                    "flavourKey": "nsAgv_fk",
                    "vnfProfile": [
                        {
                            "vnfProfileId": "vAgv_profile",
                            "vnfdId": "masterPLC",
                            "flavourId": "masterPLC_df",
                            "instantiationLevel": "masterPLC_il",
                            "minNumberOfInstances": 1,
                            "maxNumberOfInstances": 3,
                            "nsVirtualLinkConnectivity": [
                                {
                                    "virtualLinkProfileId": "agv_vl_profile_data",
                                    "cpdId": [
                                        "masterPLC_data_ext"
                                    ]
                                }
                            ]
                        }
                    ],
                    "pnfProfile": [
                        {
                            "pnfProfileId": "EPC_profile",
                            "pnfdId": "EPC",
                            "pnfVirtualLinkConnectivity": [
                                {
                                    "virtualLinkProfileId": "agv_vl_profile_data",
                                    "cpdId": [
                                        "EPC_data"
                                    ]
                                }
                            ]
                        }
                    ],
                    "virtualLinkProfile": [
                        {
                            "virtualLinkProfileId": "agv_vl_profile_data",
                            "virtualLinkDescId": "agv_vl_data",
                            "flavourId": "agv_vl_df_data",
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
                            "nsLevelId": "nsAgv_il",
                            "description": "Default instantiation level for the AGV service",
                            "vnfToLevelMapping": [
                                {
                                    "vnfProfileId": "masterPLC_profile",
                                    "numberOfInstances": 1
                                }
                            ],
                            "virtualLinkToLevelMapping": [
                                {
                                    "virtualLinkProfileId": "agv_vl_profile_data",
                                    "bitRateRequirements": {
                                        "root": "1",
                                        "leaf": "1"
                                    }
                                }
                            ]
                        }
                    ],
                    "defaultNsInstantiationLevelId": "nsAgv_il",
                    "dependencies": []
                }
            ],
            "security": {
                "signature": "FC_NSD_SIGNATURE",
                "algorithm": "FC_NSD_ALGORITHM",
                "certificate": "FC_NSD_CERTIFICATE"
            }
        }
    ],
    "translationRules": [
        {
            "nsdId": "nsAstiAgvControl",
            "nsdVersion": "0.1",
            "nsFlavourId": "nsAgv_df",
            "nsInstantiationLevelId": "nsAgv_il",
            "input": [
                {
                    "parameterId": "sensor_amount",
                    "minValue": "1",
                    "maxValue": "5"
                },
                {
                    "parameterId": "vehicle_amount",
                    "minValue": "1",
                    "maxValue": "5"
                }
            ]
        }
    ]
}'
