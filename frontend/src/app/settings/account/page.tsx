import { Separator } from "@/components/ui/separator"
import { AccountForm } from "./account-form"
import SettingPageWrapper from "@/app/settings/components/setting-page-wrapper";

export default function SettingsAccountPage() {
  return (
    <SettingPageWrapper
      title="Account"
      description="Update your account settings. Set your preferred language and timezone.">
      <AccountForm />
    </SettingPageWrapper>

  )
}
