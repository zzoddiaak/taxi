package contracts

org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'GET'
        urlPath('/api/v1/drivers/1') {
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
                firstName: "Jane",
                lastName: "Smith",
                email: "jane.smith@example.com",
                phoneNumber: "+9876543210",
                licenseNumber: "DL123456",
                car: [
                        id: 1,
                        model: "Tesla Model 3",
                        licensePlate: "ABC123"
                ],
                rating: [
                        averageRating: 4.9,
                        ratingCount: 100
                ]
        ])
        headers {
            contentType('application/json')
        }
    }
}