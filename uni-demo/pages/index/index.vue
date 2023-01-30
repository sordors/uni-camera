<template>
	<view>
		<view class="bg-white page-container">
			<view class="title">人像面蒙版（内置）</view>
			<view class="box">
				<button @click="takePhotoFront(0)" class="box-btn">人像面蒙版1</button>
				<button @click="takePhotoFront(1)" class="box-btn">人像面蒙版2</button>
				<button @click="takePhotoFront(2)" class="box-btn">人像面蒙版3</button>
			</view>
			<view class="title">国徽面蒙版（内置）</view>
			<view class="box">
				<button @click="takePhotoBack(0)" class="box-btn">国徽面蒙版1</button>
				<button @click="takePhotoBack(1)" class="box-btn">国徽面蒙版2</button>
				<button @click="takePhotoBack(2)" class="box-btn">国徽面蒙版3</button>
			</view>

			<view class="title">自定义1</view>
			<view class="box">
				<button @click="custom1" class="box-btn">取消手电筒</button>
				<button @click="custom2" class="box-btn">取消文字</button>
				<button @click="custom3" class="box-btn">自定义文字</button>
			</view>

			<view class="title">自定义2</view>
			<view class="box">
				<button @click="custom4" class="box-btn">竖屏模式</button>
				<button @click="custom5" class="box-btn">无蒙版竖屏</button>
				<button @click="custom6" class="box-btn">无蒙版横屏</button>
			</view>

			<view class="title">自定义3</view>
			<view class="box">
				<button @click="custom7" class="box-btn">背景黑色</button>
				<button @click="custom8" class="box-btn">背景透明</button>
				<button @click="custom9" class="box-btn">自定义蒙版</button>
			</view>
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
		//内置人像面蒙版 0：蒙版第一张,1：蒙版第二张，2,蒙版第三张
		takePhotoFront(imageIndex) {
			let _this = this;
			cameraModule.takePhoto({ type: 0, imageIndex: imageIndex }, res => {
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
		//内置国徽面蒙版 0：蒙版第一张,1：蒙版第二张，2,蒙版第三张
		takePhotoBack(imageIndex) {
			let _this = this;
			cameraModule.takePhoto({ type: 1, imageIndex: imageIndex }, res => {
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
		//取消手电筒
		custom1() {
			let _this = this;
			cameraModule.takePhoto({ type: 1, imageIndex: 1, enableTorch: false }, res => {
				console.log(res);
				_this.src = 'file://' + res.file;
			});
		},
		//取消文字
		custom2() {
			let _this = this;
			cameraModule.takePhoto({ type: 1, imageIndex: 1, text: '' }, res => {
				console.log(res);
				_this.src = 'file://' + res.file;
			});
		},
		//自定义文字
		custom3() {
			let _this = this;
			cameraModule.takePhoto({ type: 1, imageIndex: 1, text: '点我点我' }, res => {
				console.log(res);
				_this.src = 'file://' + res.file;
			});
		},
		//竖屏切换
		custom4() {
			let _this = this;
			cameraModule.takePhoto({ type: 0, imageIndex: 1, text: '请拍摄身份证人像面', landscape: false }, res => {
				console.log(res);
				_this.src = 'file://' + res.file;
			});
		},
		//无蒙版竖屏
		custom5() {
			let _this = this;
			//设置type为不存在值的即可
			cameraModule.takePhoto({ type: 99, text: '请拍摄身份证人像面', landscape: false }, res => {
				console.log(res);
				_this.src = 'file://' + res.file;
			});
		},
		//无蒙版横屏
		custom6() {
			let _this = this;
			//设置type为不存在值的即可
			cameraModule.takePhoto({ type: 99, text: '请拍摄身份证人像面' }, res => {
				console.log(res);
				_this.src = 'file://' + res.file;
			});
		},
		//自定义颜色
		custom7() {
			let _this = this;
			//设置type为不存在值的即可
			cameraModule.takePhoto({ type: 1, imageIndex: 1, backColor: '#000000' }, res => {
				console.log(res);
				_this.src = 'file://' + res.file;
			});
		},
		//背景透明
		custom8() {
			let _this = this;
			//设置type为不存在值的即可
			cameraModule.takePhoto({ type: 1, imageIndex: 1, backColor: '' }, res => {
				console.log(res);
				_this.src = 'file://' + res.file;
			});
		},
		/*自定义蒙版，type=2
		 * 1.创建对应目录/nativeplugins/yun-camerax/android/assets
		 * 2.将分辨率为890x560的png图片放入该目录，eg：比如放入test.png，即可
		 * 3.请按照示例中对应的文件制作对应蒙版，否则裁剪图片异常
		 */
		custom9() {
			let _this = this;
			//将放入的蒙版图片写在backgroundImage上，这样就行了
			cameraModule.takePhoto({ type: 2, backgroundImage: 'test2.png', fullSrc: true }, res => {
				//_this.src = 'file://' + res.file;
				//设置了fullSrc，就不需要自行添加'file://'
				_this.src = res.file;
				
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
			});
		}
	}
};
</script>
<style lang="scss" scoped>
.title {
	height: 40px;
	line-height: 40px;
	margin-left: 10px;
	font-size: 32rpx;
	font-weight: bold;
}
.box {
	display: flex;
	.box-btn {
		font-size: 28rpx;
		background-color: #0095ffc9;
		color: #fff;
		width: calc(100% - 80rpx);
		margin-left: 20rpx;
	}
	.box-btn:last-child {
		margin-right: 20rpx;
	}
}
</style>
