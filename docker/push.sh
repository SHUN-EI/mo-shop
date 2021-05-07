#登录阿里云私有镜像仓库
docker login --username=xking天道酬勤 registry.cn-shenzhen.aliyuncs.com --password=mo80437628
#获取当前时间 v-2021-10-10-13-12
time=$(date "+%Y-%m-%d-%H-%M-%S")
#构建整个项目，或者单独构建common项目，避免依赖未被构建上去
cd ../mo-common
mvn install

#构建网关服务
cd ../mo-gateway
mvn install -Dmaven.test.skip=true dockerfile:build
#docker 打包并推送到远程仓库
docker tag mo-cloud/mo-gateway:latest registry.cn-shenzhen.aliyuncs.com/mo-cloud/mo-gateway:v-$time
docker push registry.cn-shenzhen.aliyuncs.com/mo-cloud/mo-gateway:v-$time
echo "网关构建并推送成功"

#构建用户服务
cd ../mo-user-service
mvn install -Dmaven.test.skip=true dockerfile:build
#docker 打包并推送到远程仓库
docker tag mo-cloud/mo-user-service:latest registry.cn-shenzhen.aliyuncs.com/mo-cloud/mo-user-service:v-$time
docker push registry.cn-shenzhen.aliyuncs.com/mo-cloud/mo-user-service:v-$time
echo "用户服务构建并推送成功"

#构建优惠券服务
cd ../mo-coupon-service
mvn install -Dmaven.test.skip=true dockerfile:build
#docker 打包并推送到远程仓库
docker tag mo-cloud/mo-coupon-service:latest registry.cn-shenzhen.aliyuncs.com/mo-cloud/mo-coupon-service:v-$time
docker push registry.cn-shenzhen.aliyuncs.com/mo-cloud/mo-coupon-service:v-$time
echo "优惠券服务构建并推送成功"

#构建购物车服务
cd ../mo-cart-service
mvn install -Dmaven.test.skip=true dockerfile:build
#docker 打包并推送到远程仓库
docker tag mo-cloud/mo-cart-service:latest registry.cn-shenzhen.aliyuncs.com/mo-cloud/mo-cart-service:v-$time
docker push registry.cn-shenzhen.aliyuncs.com/mo-cloud/mo-cart-service:v-$time
echo "购物车服务构建并推送成功"

#构建商品服务
cd ../mo-product-service
mvn install -Dmaven.test.skip=true dockerfile:build
#docker 打包并推送到远程仓库
docker tag mo-cloud/mo-product-service:latest registry.cn-shenzhen.aliyuncs.com/mo-cloud/mo-product-service:v-$time
docker push registry.cn-shenzhen.aliyuncs.com/mo-cloud/mo-product-service:v-$time
echo "商品服务构建并推送成功"

#构建订单服务
cd ../mo-order-service
mvn install -Dmaven.test.skip=true dockerfile:build
#docker 打包并推送到远程仓库
docker tag mo-cloud/mo-order-service:latest registry.cn-shenzhen.aliyuncs.com/mo-cloud/mo-order-service:v-$time
docker push registry.cn-shenzhen.aliyuncs.com/mo-cloud/mo-order-service:v-$time
echo "订单服务构建并推送成功"


echo "=========================================构建脚本执行完毕========================================="

