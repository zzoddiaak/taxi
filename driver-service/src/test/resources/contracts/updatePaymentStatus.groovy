package contracts

org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'PUT'
        url 'api/payments/1/status'
        body([
                status: 'complete'
        ])
        headers {
            contentType('application/json')
        }
    }
    response {
        status 200
    }
}
