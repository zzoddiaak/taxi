package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Should update driver rating")
    request {
        method 'PUT'
        url '/api/v1/drivers/1/rating'
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