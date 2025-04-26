import React from 'react';
import Link from "next/link";
import Image from "next/image";
import {
	DropdownMenu,
	DropdownMenuContent,
	DropdownMenuItem,
	DropdownMenuTrigger
} from "@/components/ui/dropdown-menu";
import {Button} from "@/components/ui/button";
import {
	ChevronDownIcon,
	MoreHorizontal,
	Search,
} from "lucide-react";
import {Input} from "@/components/ui/input";
import {Icon} from "@/components/ui/icons";
import {MenuItemProps} from "@/app/typing";
import {useAppSelector} from "@/stores/hooks";
import {Side} from "@floating-ui/utils";
import AvatarDropdown from "@/components/AvatarDropdown";
import {ChevronRightIcon} from "@radix-ui/react-icons";
import useAccess from "@/hooks/useAccess";

const MenuItem: React.FC<{ item: MenuItemProps, menuSide?: Side, depth?: number }> = ({
  item,
  menuSide = "bottom",
  depth = 0
}) => {
	const {canAccess} = useAccess(item.access);

	// 如果没有访问权限，直接不渲染
	if (!canAccess) {
		return null;
	}

	const itemBaseClass = "flex items-center space-x-2 transition-colors hover:text-foreground/80 text-foreground/60 p-2 rounded-md w-full";

	const renderContent = () => (
		<>
			{item.icon && <Icon icon={item.icon} className="h-4 w-4"/>}
			<span>{item.name}</span>
			{item.children && item.children.length > 0 && (
				depth === 0 ? <ChevronDownIcon className="h-4 w-4 ml-1"/> : <ChevronRightIcon className="h-4 w-4 ml-1"/>
			)}
		</>
	);

	if (item.target) {
		return (
			<Button variant="ghost" asChild className={itemBaseClass}>
				<a href={item.path} target={item.target}>
					{renderContent()}
				</a>
			</Button>
		);
	}

	if (item.children && item.children.length > 0) {
		return (
			<DropdownMenu>
				<DropdownMenuTrigger asChild>
					<Button variant="ghost" className={itemBaseClass}>
						{renderContent()}
					</Button>
				</DropdownMenuTrigger>
				<DropdownMenuContent side={depth === 0 ? "bottom" : "right"} align={depth === 0 ? "start" : "end"}>
					{item.children.map((child, index) => (
						<DropdownMenuItem key={index} asChild>
							<MenuItem item={child} menuSide="right" depth={depth + 1}/>
						</DropdownMenuItem>
					))}
				</DropdownMenuContent>
			</DropdownMenu>
		);
	}

	return (
		<Button variant="ghost" asChild className={itemBaseClass}>
			<Link href={item.path}>
				{renderContent()}
			</Link>
		</Button>
	);
};


const HeaderPage: React.FC = () => {
	const {
		navigationMenu,
		showSearch,
		isHeaderSticky,
		layout
	} = useAppSelector(state => state.Layout)
	const visibleNavItems = navigationMenu.items.slice(0, navigationMenu.collapsedRow)
	const collapsedNavItems = navigationMenu.items.slice(navigationMenu.collapsedRow)


	return (<>
			<header
				className={`flex h-15 items-center bg-background/95 px-8 md:px-12 ${
					isHeaderSticky ? "sticky top-0 z-50 backdrop-blur supports-[backdrop-filter]:bg-background/60" : ""
				}`}
			>
				<div className="ml-12 container flex h-14 items-center">
					<div className="mr-12 flex items-center">
						<Link href="/" className="flex items-center space-x-2">
							{typeof layout.logo === 'string' ? (
								<Image src={layout.logo} alt={layout.title} width={32} height={32}/>
							) : (
								layout.logo
							)}
							<span className="ml-3 hidden font-bold sm:inline-block whitespace-nowrap">
              Next Template App
            </span>
						</Link>
					</div>
					<nav className="hidden flex-row items-center gap-6 text-sm font-medium md:flex">
						{visibleNavItems.map((item) => {
							return (
								<MenuItem key={item.name} item={item}/>
							)
						})}
						{collapsedNavItems.length > 0 && (
							<DropdownMenu>
								<DropdownMenuTrigger asChild>
									<Button variant="ghost" size="icon" className="p-2 rounded-md">
										<MoreHorizontal className="h-4 w-4"/>
										<span className="sr-only">更多选项</span>
									</Button>
								</DropdownMenuTrigger>
								<DropdownMenuContent align="end">
									{collapsedNavItems.map((item) => (
										<DropdownMenuItem key={item.name} asChild>
											<MenuItem item={item} depth={1}/>
										</DropdownMenuItem>
									))}
								</DropdownMenuContent>
							</DropdownMenu>
						)}
					</nav>
					<div className="flex w-full items-center gap-4 md:ml-auto md:gap-2 lg:gap-4">
						{showSearch && (
							<form className="ml-auto flex-1 sm:flex-initial">
								<div className="relative">
									<Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground"/>
									<Input
										type="search"
										placeholder="Search products..."
										className="pl-8 sm:w-[300px] md:w-[200px] lg:w-[300px]"
									/>
								</div>
							</form>
						)}
						<AvatarDropdown/>
					</div>
				</div>
			</header>
		</>
	)
}

export default HeaderPage;
