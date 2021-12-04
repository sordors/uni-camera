<template>
	<view>
		<view class="bg-white page-container">
			<view class="text-center"><button @click="location">定位测试</button></view>
			<view class="text-center"><button @click="demo1">人像面蒙版1</button></view>
			<view class="text-center"><button @click="demo2">人像面蒙版2</button></view>
			<view class="text-center"><button @click="demo3">人像面蒙版3</button></view>
			<view class="text-center"><button @click="backDemo1">国徽面蒙版1</button></view>
			<view class="text-center"><button @click="backDemo2">国徽面蒙版3</button></view>
			<view class="text-center"><button @click="backDemo3">国徽面蒙版3</button></view>
			<view class="text-center"><button @click="custom1">取消手电筒</button></view>
			<view class="text-center"><button @click="custom2">取消文字</button></view>
			<view class="text-center"><button @click="custom3">自定义文字</button></view>
			<view class="text-center"><button @click="custom4">竖屏模式</button></view>
			<view class="text-center"><button @click="custom5">无蒙版竖屏模式</button></view>
			<view class="text-center"><button @click="custom6">无蒙版横屏模式</button></view>
			<view style="margin-top: 20px;"><image :src="src" style="width: 100%;" @error="imageError"></image></view>
		</view>
	</view>
</template>
<script>
var cameraModule = uni.requireNativePlugin('yun-camerax-module');
export default {
	data() {
		return {
			init: false,
			src: ''
		};
	},
	methods: {
		imageError(e) {
			console.error('image发生error事件，携带值为' + e.detail.errMsg);
		},
		location() {
			let _this = this;
			uni.getLocation({
				type: 'gcj02',
				success: function(data) {
					uni.showModal({
						title: '提示',
						content: '当前位置的经度：' + data.longitude + '当前位置的纬度：' + data.latitude,
						success: function(res) {
							if (res.confirm) {
								_this.copyData(data.longitude + ',' + data.latitude);
								console.log('用户点击确定');
							} else if (res.cancel) {
								console.log('用户点击取消');
							}
						}
					});
					console.log('当前位置的经度：' + data.longitude);
					console.log('当前位置的纬度：' + data.latitude);
				}
			});
		},
		copyData(content) {
			uni.setClipboardData({
				data: content,
				success: function() {
					uni.showToast({
						title: '复制成功',
						mask: false,
						duration: 2000,
						icon: 'none'
					});
				}
			});
		},
		demo1() {
			let _this = this;
			cameraModule.takePhoto({ type: 0, imageIndex: 0 }, res => {
				console.log(res);
				uni.showModal({
					title: '提示',
					content: JSON.stringify(res),
					success: function(res) {
				 	if (res.confirm) {
							console.log('用户点击确定');
						} else if (res.cancel) {
							console.log('用户点击取消');
						}
					}
				});
				_this.src = 'file://' + res.file;
			});
		},
		demo2() {
			let _this = this;
			cameraModule.takePhoto({ type: 0, imageIndex: 1 }, res => {
				console.log(res);
				_this.src = 'file://' + res.file;
			});
		},
		demo3() {
			let _this = this;
			cameraModule.takePhoto({ type: 0, imageIndex: 2 }, res => {
				console.log(res);
				_this.src = 'file://' + res.file;
			});
		},
		backDemo1() {
			let _this = this;
			cameraModule.takePhoto({ type: 1, imageIndex: 0 }, res => {
				console.log(res);
				_this.src = 'file://' + res.file;
			});
		},
		backDemo2() {
			let _this = this;
			cameraModule.takePhoto({ type: 1, imageIndex: 1 }, res => {
				console.log(res);
				_this.src = 'file://' + res.file;
			});
		},
		backDemo3() {
			let _this = this;
			cameraModule.takePhoto({ type: 1, imageIndex: 2 }, res => {
				console.log(res);
				_this.src = 'file://' + res.file;
			});
		},
		custom1() {
			let _this = this;
			cameraModule.takePhoto({ type: 1, imageIndex: 1, enableTorch: false }, res => {
				console.log(res);
				_this.src = 'file://' + res.file;
			});
		},
		custom2() {
			let _this = this;
			cameraModule.takePhoto({ type: 1, imageIndex: 1, text: '' }, res => {
				console.log(res);
				_this.src = 'file://' + res.file;
			});
		},
		custom3() {
			let _this = this;
			cameraModule.takePhoto({ type: 1, imageIndex: 1, text: '点我点我' }, res => {
				console.log(res);
				_this.src = 'file://' + res.file;
			});
		},
		custom4() {
			let _this = this;
			cameraModule.takePhoto({ type: 0, imageIndex: 1, text: '请拍摄身份证人像面', landscape: false }, res => {
				console.log(res);
				_this.src = 'file://' + res.file;
			});
		},
		custom5() {
			let _this = this;
			//设置type为不存在值的即可
			cameraModule.takePhoto({ type: 99, text: '请拍摄身份证人像面', landscape: false }, res => {
				console.log(res);
				_this.src = 'file://' + res.file;
			});
		},
		custom6() {
			let _this = this;
			//设置type为不存在值的即可
			cameraModule.takePhoto({ type: 99, text: '请拍摄身份证人像面' }, res => {
				console.log(res);
				_this.src = 'file://' + res.file;
			});
		}
	}
};
</script>
<style lang="scss" scoped></style>
