import React from "react";

const BarWave: React.FC<{
    className?: string;
    color?: string;
    width?: number | string;
    height?: number | string;
    duration?: string;
}> = ({
          className = "",
          color = "#151515",
          width = "2rem",
          height = "1rem",
          duration = "1s",
      }) => {
    const resolvedWidth = typeof width === "number" ? `${width}px` : width;
    const resolvedHeight = typeof height === "number" ? `${height}px` : height;
    const barWidth = `calc(${resolvedWidth} / 4 * 3 / 4)`;

    // Inline styles to include custom animation properties
    const spanStyles = {
        backgroundColor: color,
        width: barWidth,
        height: resolvedHeight,
    };

    return (
        // Overlay container to cover the entire screen
        <div className="fixed inset-0 bg-black bg-opacity-20 flex justify-center items-center z-10">
            {/* Inner container to center the loading bars */}
            <div
                className={`flex flex-row justify-between items-center w-[${resolvedWidth}] ${className}`}
                style={{ width: resolvedWidth }}
            >
                {Array.from({ length: 4 }).map((_, index) => (
                    <span
                        key={index}
                        className={`animate-wave${index + 1}`}
                        style={{
                            ...spanStyles,
                            animationDuration: duration,
                            animationDelay: `${-0.15 * index}s`,
                        }}
                    ></span>
                ))}
            </div>
            <style>
                {`
                    @keyframes wave {
                        0%, 100% {
                            transform: scaleY(1);
                        }
                        50% {
                            transform: scaleY(2);
                        }
                    }
                    .animate-wave1 {
                        animation: wave ${duration} -0.45s ease-in-out infinite;
                    }
                    .animate-wave2 {
                        animation: wave ${duration} -0.3s ease-in-out infinite;
                    }
                    .animate-wave3 {
                        animation: wave ${duration} -0.15s ease-in-out infinite;
                    }
                    .animate-wave4 {
                        animation: wave ${duration} ease-in-out infinite;
                    }
                `}
            </style>
        </div>
    );
};

export default BarWave;
