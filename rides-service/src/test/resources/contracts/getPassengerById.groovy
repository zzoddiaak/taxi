package contracts

org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'GET'
        urlPath('/api/passengers/1') {
            queryParameters {
                parameter("id", "1")
            }
        }
        headers {
            contentType('application/json')
        }
    }
    response {
        status 200
        body([
                id: 1,
                firstName: "John",
                lastName: "Doe",
                email: "john.doe@example.com",
                phoneNumber: "+1234567890",
                rating: [
                        averageRating: 4.8,
                        ratingCount: 42
                ],
                driverRating: 4.5
        ])
        headers {
            contentType('application/json')
        }
    }
}