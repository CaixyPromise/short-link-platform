import {useRouter} from "next/router";
import {NavItem} from "../../config/menu";
import {MenuItemProps} from "@/app/typing";

// 查找当前路径匹配的菜单项
const findRouteInfo = (items: MenuItemProps[], currentPath: string, ancestors:MenuItemProps[] = []): {
    currentItem: MenuItemProps,
    ancestors: MenuItemProps[]
} | null => {
    for (const item of items) {
        // 当前项匹配路径
        if (item.path === currentPath) {
            return { currentItem: item, ancestors };
        }
        // 递归搜索子菜单
        if (item.children) {
            const result = findRouteInfo(item.children, currentPath, [...ancestors, item]);
            if (result) return result;
        }
    }
    return null; // 如果没有匹配的路径，返回 null
};

export const useCurrentRoute = (currentPath: string) => {
    return findRouteInfo(NavItem, currentPath);
}
