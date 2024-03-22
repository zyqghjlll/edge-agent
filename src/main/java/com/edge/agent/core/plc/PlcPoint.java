package com.edge.agent.core.plc;

import lombok.Data;

/**
 * @author zyq
 */
@Data
public class PlcPoint {
    private Long id;
    private Long pkPlc;
    private String endpointUrl;
    private String namespaceIndex;
    private String identifier;
    private String pointType;
    private int sort;
}
