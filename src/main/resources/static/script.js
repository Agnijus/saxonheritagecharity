class GlobalCustomException extends Error {
    constructor(message, statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
const currentPage = window.location.pathname;
const navLinks = document.querySelectorAll('.nav-link');

navLinks.forEach(link => {
    if (link.getAttribute('href') === currentPage) {
        link.classList.add('active');
    }
});
window.addEventListener("load", () => {
    window.addEventListener("resize", () => {
        if (window.innerWidth < 660) {
            window.resizeTo(660, window.innerHeight);
        }
    });
});



//
// MemberService
//


// Member Register Form
document.getElementById("register-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;

    const requestBody = `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.saxonheritage.example.com/">
                <soapenv:Header/>
                <soapenv:Body>
                    <ser:registerMember>
                        <ser:arg0>${name}</ser:arg0>
                        <ser:arg1>${email}</ser:arg1>
                    </ser:registerMember>
                </soapenv:Body>
            </soapenv:Envelope>`;

    const responseElement = document.getElementById("register-result");
    try {
        const response = await fetch("/soap-api/memberService", {
            method: "POST",
            headers: {
                "Content-Type": "text/xml"
            },
            body: requestBody
        });

        const responseText = await response.text();
        responseElement.innerHTML = `<pre>${responseText}</pre>`;
    } catch (error) {
        responseElement.textContent = `Error: ${error.message}`;
    }
});

// Update Member Information
document.getElementById("update-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const memberId = document.getElementById("member-id").value;
    const newEmail = document.getElementById("new-email").value;

    const requestBody = `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.saxonheritage.example.com/">
                <soapenv:Header/>
                <soapenv:Body>
                    <ser:updateMemberDetails>
                        <ser:arg0>${memberId}</ser:arg0>
                        <ser:arg1>${newEmail}</ser:arg1>
                    </ser:updateMemberDetails>
                </soapenv:Body>
            </soapenv:Envelope>`;

    const responseElement = document.getElementById("update-result");
    try {
        const response = await fetch("/soap-api/memberService", {
            method: "POST",
            headers: {
                "Content-Type": "text/xml"
            },
            body: requestBody
        });

        const responseText = await response.text();
        responseElement.innerHTML = `<pre>${responseText}</pre>`;
    } catch (error) {
        responseElement.textContent = `Error: ${error.message}`;
    }
});

// Get Member Information
document.getElementById("info-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const memberId = document.getElementById("info-member-id").value;

    const requestBody = `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.saxonheritage.example.com/">
                <soapenv:Header/>
                <soapenv:Body>
                    <ser:getMembershipInfo>
                        <ser:arg0>${memberId}</ser:arg0>
                    </ser:getMembershipInfo>
                </soapenv:Body>
            </soapenv:Envelope>`;

    const responseElement = document.getElementById("info-result");
    try {
        const response = await fetch("/soap-api/memberService", {
            method: "POST",
            headers: {
                "Content-Type": "text/xml"
            },
            body: requestBody
        });

        const responseText = await response.text();
        responseElement.innerHTML = `<pre>${responseText}</pre>`;
    } catch (error) {
        responseElement.textContent = `Error: ${error.message}`;
    }
});




// // Member Update Information
document.getElementById("update-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const memberId = document.getElementById("member-id").value;
    const newEmail = document.getElementById("new-email").value;

    const requestBody = `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.saxonheritage.example.com/">
                <soapenv:Header/>
                <soapenv:Body>
                    <ser:updateMemberDetails>
                        <ser:arg0>${memberId}</ser:arg0>
                        <ser:arg1>${newEmail}</ser:arg1>
                    </ser:updateMemberDetails>
                </soapenv:Body>
            </soapenv:Envelope>`;

    const responseElement = document.getElementById("update-result");
    try {
        const response = await fetch("/soap-api/memberService", {
            method: "POST",
            headers: {
                "Content-Type": "text/xml"
            },
            body: requestBody
        });

        const responseText = await response.text();
        responseElement.innerHTML = `<pre>${responseText}</pre>`;
    } catch (error) {
        responseElement.textContent = `Error: ${error.message}`;
    }
});

// Get member information
document.getElementById("info-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const memberId = document.getElementById("info-member-id").value;

    const requestBody = `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.saxonheritage.example.com/">
                <soapenv:Header/>
                <soapenv:Body>
                    <ser:getMembershipInfo>
                        <ser:arg0>${memberId}</ser:arg0>
                    </ser:getMembershipInfo>
                </soapenv:Body>
            </soapenv:Envelope>`;

    const responseElement = document.getElementById("info-result");
    try {
        const response = await fetch("/soap-api/memberService", {
            method: "POST",
            headers: {
                "Content-Type": "text/xml"
            },
            body: requestBody
        });

        const responseText = await response.text();
        responseElement.innerHTML = `<pre>${responseText}</pre>`;
    } catch (error) {
        responseElement.textContent = `Error: ${error.message}`;
    }
});

// Member Insert Visitor Report
document.getElementById("report-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const memberId = document.getElementById("report-member-id").value;
    const visitorReport = document.getElementById("visitor-report").value;

    const requestBody = `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.saxonheritage.example.com/">
    <soapenv:Header/>
    <soapenv:Body>
      <ser:submitVisitorReport>
        <ser:arg0>${memberId}</ser:arg0>
        <ser:arg1>
          <item>${visitorReport}</item>
        </ser:arg1>
      </ser:submitVisitorReport>
    </soapenv:Body>
  </soapenv:Envelope>`;

    const responseElement = document.getElementById("report-result");
    try {
        const response = await fetch("/soap-api/memberService", {
            method: "POST",
            headers: {
                "Content-Type": "text/xml",
            },
            body: requestBody,
        });

        if (response.ok) {
            responseElement.textContent = "Visitor report submitted successfully.";
        } else {
            const responseText = await response.text();
            const parser = new DOMParser();
            const xmlDoc = parser.parseFromString(responseText, "text/xml");
            const faultCode = xmlDoc.getElementsByTagName("faultcode")[0]?.textContent;
            const faultString = xmlDoc.getElementsByTagName("faultstring")[0]?.textContent;
            if (faultCode === "soap:Server" && faultString.startsWith("Member not found")) {
                throw new GlobalCustomException("Member not found.", 404);
            }
            responseElement.innerHTML = `<pre>${faultString}</pre>`;
        }
    } catch (error) {
        if (error instanceof GlobalCustomException) {
            responseElement.textContent = `${error.message}`;
            console.log(error);
        } else {
            responseElement.textContent = `${error.message}`;
        }
    }
});





// Member Get Visitor Reports
document.getElementById("visitor-report-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const memberId = document.getElementById("visitor-report-member-id").value;

    const requestBody = `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.saxonheritage.example.com/">
<soapenv:Header/>
<soapenv:Body>
    <ser:getVisitorReports>
        <ser:arg0>${memberId}</ser:arg0>
    </ser:getVisitorReports>
</soapenv:Body>
</soapenv:Envelope>`;

    const responseElement = document.getElementById("visitor-report-result");
    try {
        const response = await fetch("/soap-api/memberService", {
            method: "POST",
            headers: {
                "Content-Type": "text/xml"
            },
            body: requestBody
        });

        const responseText = await response.text();
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(responseText, "text/xml");

        if (response.ok) {
            const reports = xmlDoc.getElementsByTagName("item");
            let result = "<ul>";
            for (let i = 0; i < reports.length; i++) {
                let reportText = reports[i].textContent.trim();
                if (reportText !== "") {
                    result += `<li>${reportText}</li>`;
                }
            }
            result += "</ul>";
            responseElement.innerHTML = result;
        } else {
            const faultString = xmlDoc.getElementsByTagName("faultstring")[0].textContent;
            responseElement.innerHTML = `<pre>${faultString}</pre>`;
        }

    } catch (error) {
        responseElement.innerHTML = `${error.message}`;
    }
});



// Member Submit Promotion
document.getElementById("promotion-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const memberId = document.getElementById("promotion-member-id").value;
    const promotion = document.getElementById("promotion").value;

    const requestBody = `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.saxonheritage.example.com/">
            <soapenv:Header/>
            <soapenv:Body>
                <ser:sendPromotion>
                    <ser:arg0>${memberId}</ser:arg0>
                    <ser:arg1>${promotion}</ser:arg1>
                </ser:sendPromotion>
            </soapenv:Body>
        </soapenv:Envelope>`;

    const responseElement = document.getElementById("promotion-result");
    try {
        const response = await fetch("/soap-api/memberService", {
            method: "POST",
            headers: {
                "Content-Type": "text/xml"
            },
            body: requestBody
        });

        if (response.ok) {
            responseElement.textContent = "Promotion sent successfully.";
        } else {
            const responseText = await response.text();
            const parser = new DOMParser();
            const xmlDoc = parser.parseFromString(responseText, "text/xml");
            const faultCode = xmlDoc.getElementsByTagName("faultcode")[0]?.textContent;
            const faultString = xmlDoc.getElementsByTagName("faultstring")[0]?.textContent;
            if (faultCode === "soap:Server" && (faultString.startsWith("Member not found") || faultString.startsWith("Invalid member ID"))) {
                responseElement.textContent = "Member not found.";
            } else {
                responseElement.innerHTML = `<pre>${faultString}</pre>`;
            }
        }
    } catch (error) {
        responseElement.textContent = `${error.message}`;
    }
});



// Get Promotions Form
// document.getElementById("promotions-form").addEventListener("submit", async (event) => {
//     event.preventDefault();
//     const memberId = document.getElementById("promotions-member-id").value;
//
//     const requestBody = `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://services.saxonheritage.example.com/">
//     <soapenv:Header/>
//     <soapenv:Body>
//         <ser:getPromotions>
//             <arg0>${memberId}</arg0>
//         </ser:getPromotions>
//     </soapenv:Body>
// </soapenv:Envelope>`;
//
//
//     const responseElement = document.getElementById("promotions-result");
//     try {
//         const response = await fetch("/soap-api/memberService", {
//             method: "POST",
//             headers: {
//                 "Content-Type": "text/xml"
//             },
//             body: requestBody
//         });
//
//         const responseText = await response.text();
//         const parser = new DOMParser();
//         const xmlDoc = parser.parseFromString(responseText, "text/xml");
//
//         if (response.ok) {
//             const promotions = xmlDoc.getElementsByTagName("item");
//             let result = "<ul>";
//             for (let i = 0; i < promotions.length; i++) {
//                 result += `<li>${promotions[i].textContent}</li>`;
//             }
//             result += "</ul>";
//             responseElement.innerHTML = result;
//         } else {
//             const faultString = xmlDoc.getElementsByTagName("faultstring")[0].textContent;
//             responseElement.innerHTML = `<pre>${faultString}</pre>`;
//         }
//     } catch (error) {
//         responseElement.textContent = `${error.message}`;
//     }
// });

//
// BENEFIT SERVICE
//






