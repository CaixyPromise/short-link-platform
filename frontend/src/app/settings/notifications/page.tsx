import { Separator } from "@/components/ui/separator"
import { NotificationsForm } from "./notifications-form"
import SettingPageWrapper from "@/app/settings/components/setting-page-wrapper";

export default function SettingsNotificationsPage() {
  return (
    <SettingPageWrapper title="Notifications" description="Configure how you receive notifications.">
      <NotificationsForm />
    </SettingPageWrapper>
  )
}
