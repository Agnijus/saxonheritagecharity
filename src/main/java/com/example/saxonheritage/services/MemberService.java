package com.example.saxonheritage.services;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface MemberService {
    @WebMethod
    String registerMember(String name, String email);

    @WebMethod
    String updateMemberDetails(String memberId, String email);

    @WebMethod
    String getMembershipInfo(String memberId);

    @WebMethod
    void submitVisitorReport(int memberId, String visitorReport);

    @WebMethod
    void sendPromotion(int memberId, String promotion);
}