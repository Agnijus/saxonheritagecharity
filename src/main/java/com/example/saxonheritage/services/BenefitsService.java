package com.example.saxonheritage.services;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface BenefitsService {

    @WebMethod
    void addBenefit(
            @WebParam(name = "memberId") String memberId,
            @WebParam(name = "benefit") String benefit
    ) throws GlobalCustomException;

    @WebMethod
    void removeBenefit(
            @WebParam(name = "memberId") String memberId,
            @WebParam(name = "benefit") String benefit
    ) throws GlobalCustomException;

    @WebMethod
    List<String> getMemberBenefits(@WebParam(name = "memberId") String memberId) throws GlobalCustomException;
}
