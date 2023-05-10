// Agnijus Botyrius - 21466565
// Distributed Systems (CP60060E) - Assignment
// Project Title - Saxon Heritage Charity Application

package com.example.saxonheritagecharity.services;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface BenefitsService {

    @WebMethod
    void addMemberBenefit(
            @WebParam(name = "memberId") String memberId,
            @WebParam(name = "benefit") String benefit
    ) throws GlobalCustomException;

    @WebMethod
    void removeMemberBenefit(
            @WebParam(name = "memberId") String memberId,
            @WebParam(name = "benefit") String benefit
    ) throws GlobalCustomException;

    @WebMethod
    List<String> getMemberBenefits(@WebParam(name = "memberId") String memberId) throws GlobalCustomException;
}