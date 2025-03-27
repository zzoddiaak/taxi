package contracts

org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'POST'
        url('/api/payments')
        body([
                rideId: 123,
                amount: 100.50,
                currency: "USD"
        ])
        headers {
            contentType('application/json')
        }
    }
    response {
        status 201
        body([
                amount: 100.50
        ])
        headers {
            contentType('application/json')
        }
    }
}
