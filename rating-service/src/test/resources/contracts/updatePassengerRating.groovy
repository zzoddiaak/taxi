package contracts

org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'PUT'
        url '/api/passengers/1/rating'
        body([
                rating: 4.5
        ])
        headers {
            contentType('application/json')
        }
    }
    response {
        status 200
    }
}