import { requestPost, requestGet, requestPut, requestDelete } from '@/utils/request';
const project = {
  getProject: async function (params = {}) {
    return requestGet('/apps/search', params);
  },
  getProjectAll: async function (params = {}) {
    return requestGet('/apps', params);
  },
  projectAdd: async function (params = {}) {
    return requestPost('/apps', params);
  },
  projectEdit: async function (params = {}) {
    return requestPut(`/apps/${params.appId}`, params);
  },
  projectDetail: async function (params = {}) {
    return requestGet(`/apps/${params.appId}`, params);
  },
  envList: async function (params = {}) {
    return requestGet(`/apps/${params.appId}/navtree`);
  },
  publicNamespaceList: async function (params = {}) {
    return requestGet(`/app/${params.appCode}/namespaces/publicAndProtect`, params);
  },
  nameSpaceList: async function (params = {}) {
    return requestGet(`/apps/${params.appCode}/envs/${params.env}/clusters/${params.clusterName}/namespaces`);
  },
  //关联公共命名空间
  publickNameSpaceRelation: async function (params = {}) {
    return requestPost(`/apps/${params.appId}/namespaces/${params.namespacesId}/associate/${params.appEnvClusterIds}`, params);
  },
  //创建项目私有命名空间
  nameSpacePrivateAdd: async function (params = {}) {
    return requestPost(`/apps/${params.appId}/namespaces/private`, params);
  },
  //创建项目受保护命名空间
  nameSpaceProtectAdd: async function (params = {}) {
    return requestPost(`/apps/${params.appId}/namespaces/protect`, params);
  },
  //创建项目公开命名空间
  nameSpacePublicAdd: async function (params = {}) {
    return requestPost(`/apps/${params.appId}/namespaces/public`, params);
  },
  //新增配置
  configAdd: async function (params = {}) {
    return requestPost(`/item/createItem`, params);
  },
  //更新配置
  configUpdate: async function (params = {}) {
    return requestPost(`/item/updateItem`, params);
  },
  //删除配置
  configDelete: async function (params = {}) {
    return requestPost(`/item/deleteItem`, params);
  },
  createRelease: async function (params = {}) {
    return requestPost(`/createRelease`, params);
  },
  //获取回滚信息
  releasesActive: async function (params = {}) {
    return requestGet(`/namespaceId/${params.namespaceId}/releases/active`);
  },
  //获取回滚历史对比
  releasesCompare: async function (params = {}) {
    return requestGet(`/releases/compare`, params);
  },
  //回滚操作
  rollBack: async function (params = {}) {
    return requestPut(`/releases/${params.releaseId}/rollback`);
  },
  //
  nameSpaceListWithApp: async function (params = {}) {
    return requestPost(`/namespaceList`, params);
  },
  //文本编辑
  modifyItemsByTexts: async function (params = {}) {
    return requestPost(`/item/modifyItemsByTexts`, params);
  },
  //更改历史
  commitFind: async function (params = {}) {
    return requestGet(`/commit/find`, params);
  },
  //查询关联的公共命名空间
  associatedPublicNamespace: async function (params = {}) {
    return requestGet(`/envs/${params.env}/apps/${params.appCode}/clusters/${params.clusterName}/namespaces/${params.namespaceName}/associated-public-namespace`, params);
  },
  //查询项目的命名空间
  appProtectNamespace: async function (params = {}) {
    return requestGet(`/apps/${params.appId}/namespaces/${params.namespace}`);
  },
  //项目受保护命名空间授权
  authorizeProtectApp: async function (params = {}) {
    return requestPost(`/apps/${params.appId}/namespaces/${params.namespace}/authorize`, params.apps);
  },
  //同步配置diff
  syncConfigDiff: async function (params = {}) {
    return requestPost(`/item/diff`, params);
  },
  //同步操作
  syncConfig: async function (params = {}) {
    return requestPost(`/item/updateEnv`, params);
  },
};
const cluster = {
  clusterAdd: async function (params = {}) {
    return requestPost(`/apps/${params.appId}/envs/${params.env}/clusters/${params.clusterName}`);
  }
};
const instances = {
  //最新配置的实例
  getByRelease: async function (params = {}) {
    return requestPost(`/instances/by-release`, params);
  },
   //非最新配置的实例
  getByReleaseNotIn: async function (params = {}) {
    return requestGet(`/instances/namespaceId/${params.namespaceId}/releaseIds/${params.releaseIds}/by-namespace-and-releases-not-in`, params);
  },
  //所有实例
  getAllRelease: async function(params = {}) {
    return requestPost(`/instances/by-namespace`, params);
  },
  //查询最新的releaseId
  activeRelease: async function (params = {}) {
    return requestGet(`/namespaceId/${params.namespaceId}/releases/active`, params);
  },
}

export {
  project,
  cluster,
  instances
}
