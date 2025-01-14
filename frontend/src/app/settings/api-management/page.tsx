import SettingPageWrapper from "@/app/settings/components/setting-page-wrapper";
import APIPageForm from "@/app/settings/api-management/APIPageForm";

const SystemPage = () => {
	return (
		<SettingPageWrapper title="API接口设置" description="管理你的API接口与密钥">
			<APIPageForm />
		</SettingPageWrapper>
	)
}

export default SystemPage;