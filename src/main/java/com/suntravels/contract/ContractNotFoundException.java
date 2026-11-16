package com.suntravels.contract;

public class ContractNotFoundException extends RuntimeException {
    public ContractNotFoundException(Long id) {
        super("Contract " + id + " was not found");
    }
}
