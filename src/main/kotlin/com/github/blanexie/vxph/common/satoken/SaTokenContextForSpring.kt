package com.github.blanexie.vxph.common.satoken

import cn.dev33.satoken.context.SaTokenContext
import cn.dev33.satoken.context.model.SaRequest
import cn.dev33.satoken.context.model.SaResponse
import cn.dev33.satoken.context.model.SaStorage
import cn.dev33.satoken.spring.pathmatch.SaPatternsRequestConditionHolder

class SaTokenContextForSpring : SaTokenContext {

    override fun getRequest(): SaRequest {
        return SaRequestForServlet(SpringMVCUtil.getRequest())
    }

    override fun getResponse(): SaResponse {
        return SaResponseForServlet(SpringMVCUtil.getResponse())
    }

    override fun getStorage(): SaStorage {
        return SaStorageForServlet(SpringMVCUtil.getRequest())
    }

    override fun matchPath(pattern: String?, path: String?): Boolean {
        return SaPatternsRequestConditionHolder.match(pattern, path)
    }

    override fun isValid(): Boolean {
        return SpringMVCUtil.isWeb()
    }
}