package contracts

org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'GET'
        urlPath('/api/payments/ride/123') {
            queryParameters {
                parameter("rideId", "123")
            }
        }
        headers {
            contentType('application/json')
        }
    }
    response {
        status 200
        body([
                amount: 100.50
        ])
        headers {
            contentType('application/json')
        }
    }
}
