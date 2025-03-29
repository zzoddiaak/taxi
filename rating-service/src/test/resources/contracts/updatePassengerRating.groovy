package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Should update passenger rating")
    request {
        method 'PUT'
        url '/api/passengers/1/rating'
        body([
                rating: 4.5
        ])
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status 200
        body([
                success: true
        ])
        headers {
            contentType(applicationJson())
        }
    }
}