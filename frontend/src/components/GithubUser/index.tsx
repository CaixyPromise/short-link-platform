"use client"
import React, { useEffect, useState } from 'react'
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts'
import {Button} from "@/components/ui/button";
import {Github} from "lucide-react";
import {Icon} from "@/components/ui/icons";
import moment from "moment/moment";

interface GitHubUser {
    login: string;
    avatar_url: string;
    name: string;
    bio: string;
    public_repos: number;
    followers: number;
}

interface StarData {
    date: string;
    stars: number;
}

interface Contributor {
    login: string;
    avatar_url: string;
}

interface CachedData {
    user: GitHubUser | null;
    starData: StarData[];
    contributors: Contributor[];
    timestamp: number;
}

const CACHE_DURATION = 3600000; // 1 hour in milliseconds

export default function GitHubInfo({ username, repo }: { username: string, repo: string }) {
    const [user, setUser] = useState<GitHubUser | null>(null);
    const [starData, setStarData] = useState<StarData[]>([]);
    const [contributors, setContributors] = useState<Contributor[]>([]);

    useEffect(() => {
        const fetchGitHubData = async () => {
            const cacheKey = `github-info-${username}-${repo}`;
            const cachedData = localStorage.getItem(cacheKey);

            if (cachedData) {
                const parsedData: CachedData = JSON.parse(cachedData);
                const now = new Date().getTime();
                if (now - parsedData.timestamp < CACHE_DURATION) {
                    setUser(parsedData.user);
                    setStarData(parsedData.starData);
                    setContributors(parsedData.contributors);
                    return;
                }
            }

            try {
                // Fetch user data
                const userResponse = await fetch(`https://api.github.com/users/${username}`);
                if (!userResponse.ok) throw new Error('Failed to fetch user data');
                const userData: GitHubUser = await userResponse.json();

                // Fetch star history
                const starResponse = await fetch(`https://api.github.com/repos/${username}/${repo}/stargazers`, {
                    headers: {
                        'Accept': 'application/vnd.github.v3.star+json'
                    }
                });
                if (!starResponse.ok) throw new Error('Failed to fetch star data');
                const starData = await starResponse.json();

                const formattedStarData: StarData[] = starData.map((star: any, index: number) => ({
                    date: moment(star.starred_at).format('YYYY-MM-DD'),
                    stars: index + 1,
                }));

                const contributorsResponse = await fetch(`https://api.github.com/repos/${username}/${repo}/contributors`);
                if (!contributorsResponse.ok) throw new Error('Failed to fetch contributors data');
                const contributorsData: Contributor[] = await contributorsResponse.json();
                const topContributors = contributorsData.slice(0, 10);

                setUser(userData);
                setStarData(formattedStarData);
                setContributors(topContributors);

                // Cache the data
                const dataToCache: CachedData = {
                    user: userData,
                    starData: formattedStarData,
                    contributors: topContributors,
                    timestamp: new Date().getTime()
                };
                localStorage.setItem(cacheKey, JSON.stringify(dataToCache));
            } catch (error) {
                console.error('result fetching GitHub data:', error);
                setUser(null);
                setStarData([]);
                setContributors([]);
            }
        };

        fetchGitHubData();
    }, [username, repo]);

    if (!user || starData.length === 0 || contributors.length === 0) {
        return null;
    }

    return (
        <>
            <div className="container mx-auto p-4">
            <h2 className="text-2xl font-semibold mb-4">GitHub 信息</h2>
            <Card className="w-full mb-4 overflow-hidden group">
                <CardHeader className="transition-all duration-300 ease-in-out group-hover:bg-muted">
                    <CardTitle className="flex items-center gap-4">
                        <Avatar className="h-16 w-16 transition-transform duration-300 ease-in-out group-hover:scale-110">
                            <AvatarImage src={user.avatar_url} alt={user.name} />
                            <AvatarFallback>CAIXY</AvatarFallback>
                        </Avatar>
                        <div className="flex-grow">
                            <h3 className="text-xl font-semibold">{user.name}</h3>
                            <p className="text-sm text-muted-foreground">用户名：{user.login}</p>
                            <p className="text-sm text-muted-foreground">仓库名称：{repo}</p>
                        </div>
                        <Button
                            variant="outline"
                            size="icon"
                            className="transition-all duration-300 ease-in-out opacity-0 group-hover:opacity-100"
                            onClick={() => window.open(`https://www.github.com/${username}`, '_blank', 'noopener,noreferrer')}
                            aria-label={`Visit ${user.name}'s GitHub profile`}
                        >
                            <Icon icon="Github" className="h-4 w-4" />
                        </Button>
                        <Button
                            variant="outline"
                            size="icon"
                            className="transition-all duration-300 ease-in-out opacity-0 group-hover:opacity-100"
                            onClick={() => window.open(`https://www.github.com/${username}/${repo}`, '_blank', 'noopener,noreferrer')}
                            aria-label={`Visit ${user.name}'s GitHub profile`}
                        >
                            <Icon icon="WareHouse" className="h-4 w-4" />
                        </Button>
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    <p className="mb-4">{user.bio}</p>
                    <div className="flex justify-between mb-6">
                        <span>Public Repos: {user.public_repos}</span>
                        <span>Followers: {user.followers}</span>
                    </div>
                    <h4 className="font-semibold mb-2">⭐ Star Trend ⭐</h4>
                    <div className="h-48 w-full">
                        <ResponsiveContainer width="100%" height="100%">
                            <LineChart data={starData}>
                                <XAxis dataKey="date" />
                                <YAxis />
                                <Tooltip />
                                <Line type="monotone" dataKey="stars" stroke="#8884d8" />
                            </LineChart>
                        </ResponsiveContainer>
                    </div>
                    <h4 className="font-semibold mt-6 mb-2">Top Contributors</h4>
                    <div className="flex gap-2">
                        {contributors.map((contributor) => (
                            <Avatar key={contributor.login}>
                                <AvatarImage src={contributor.avatar_url} alt={contributor.login} />
                                <AvatarFallback>{contributor.login.slice(0, 2).toUpperCase()}</AvatarFallback>
                            </Avatar>
                        ))}
                    </div>
                </CardContent>
            </Card>
            </div>
        </>
    )
}