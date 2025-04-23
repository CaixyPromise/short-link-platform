"use client"
import React, {
    ReactNode,
    isValidElement,
    createContext,
    useContext,
    cloneElement,
    useEffect,
    useState,
    Fragment
} from 'react';

type ConditionalProps<T> = {
    value?: T;
    children?: ReactNode;
    strict?: boolean;
    clientOnly?: boolean;
};

type ConditionWhenProps<T> = {
    test: boolean | ((value: T) => boolean);
    else?: ReactNode;
    children?: ReactNode;
};

type ConditionElseProps = {
    children?: ReactNode;
};

type ConditionSwitchProps<T> = {
    value?: T;
    children?: ReactNode;
};

type ConditionCaseProps<T> = {
    case: T | ((value: T) => boolean);
    break?: boolean;
    children?: ReactNode;
};

type ConditionDefaultProps = {
    children?: ReactNode;
};

type ConditionNotProps<T> = {
    test: boolean | ((value: T) => boolean);
    children?: ReactNode;
};

type ConditionThrowIfProps<T> = {
    test: boolean | ((value: T) => boolean);
    throwHandler: () => void;
};

// 定义条件组件
const ConditionWhen = <T,>(props: ConditionWhenProps<T>) => null;
ConditionWhen.displayName = 'Condition.When';

const ConditionElse = (props: ConditionElseProps) => null;
ConditionElse.displayName = 'Condition.Else';

const ConditionSwitch = <T,>(props: ConditionSwitchProps<T>) => null;
ConditionSwitch.displayName = 'Condition.Switch';

const ConditionCase = <T,>(props: ConditionCaseProps<T>) => null;
ConditionCase.displayName = 'Condition.Case';

const ConditionDefault = (props: ConditionDefaultProps) => null;
ConditionDefault.displayName = 'Condition.Default';

const ConditionNot = <T,>(props: ConditionNotProps<T>) => null;
ConditionNot.displayName = 'Condition.Not';

const ConditionThrowIf = <T,>(props: ConditionThrowIfProps<T>) => null;
ConditionThrowIf.displayName = 'Condition.ThrowIf';

const Condition = {
    When: ConditionWhen,
    Else: ConditionElse,
    Switch: ConditionSwitch,
    Case: ConditionCase,
    Default: ConditionDefault,
    Not: ConditionNot,
    ThrowIf: ConditionThrowIf,
};

// 创建上下文用于传递 value
const ConditionalContext = createContext<any>(null);

function containsCondition(node: ReactNode): boolean {
    let found = false
    React.Children.forEach(node, child => {
        if (found) return
        if (isValidElement(child)) {
            const name = (child.type as any).displayName
            if (name?.startsWith('Condition.')) {
                found = true
            } else {
                found ||= containsCondition(child.props.children)
            }
        }
    })
    return found
}
function useHasMounted() {
    const [m, setM] = useState(false)
    useEffect(() => setM(true), [])
    return m
}

const Conditional = <T,>({ value, children, clientOnly, strict = false }: ConditionalProps<T>): React.ReactNode => {
    const mounted   = useHasMounted()

    const parentValue = useContext(ConditionalContext);
    /* ---------- SSR defer ---------- */
    const defer = clientOnly || containsCondition(children)
    if (!mounted && defer) {
        /* 占位而不是 null，保证 DOM 一致 */
        return <Fragment />
    }


    // 如果当前 Conditional 没有提供 value，则使用父级的 value
    const contextValue = value !== undefined ? value : parentValue;

    const isValidType = (input: any): input is T => {
        if (!strict) return true;
        if (contextValue === undefined || contextValue === null) return true;
        return typeof input === typeof contextValue;
    };

    if (!isValidType(contextValue)) {
        console.error('Type mismatch between value and conditional input.');
        return null;
    }

    const hasConditionComponent = (node: ReactNode): boolean => {
        let found = false
        React.Children.forEach(node, child => {
            if (found) return
            if (isValidElement(child)) {
                const displayName = (child.type as any).displayName
                if (displayName?.startsWith('Condition.')) {
                    found = true
                } else {
                    found = hasConditionComponent(child.props.children)
                }
            }
        })
        return found
    }



    if (!hasConditionComponent(children)) {
        if (contextValue) {
            return (
                <ConditionalContext.Provider value={contextValue}>
                    {children}
                </ConditionalContext.Provider>
            );
        } else {
            return null;
        }
    }

    const processChildren = (children: ReactNode): ReactNode => {
        let hasMatched = false;
        const output: ReactNode[] = [];

        React.Children.forEach(children, (child, index) => {
            if (!isValidElement(child)) {
                return;
            }

            const { type, props } = child;
            const displayName = (type as any).displayName;

            switch (displayName) {
                case 'Condition.When': {
                    if (hasMatched) return;
                    const conditionResult = evaluateTest(props.test, contextValue);
                    const { whenChildren, elseChildren, elseCount } = separateWhenElseChildren(props.children);

                    if (elseCount > 1) {
                        console.warn('Multiple Condition.Else detected in Condition.When. Using the last one.');
                    }

                    if (conditionResult) {
                        hasMatched = true;
                        const whenOutput = processChildren(whenChildren);
                        if (whenOutput !== null) {
                            output.push(<React.Fragment key={`When-${index}`}>{whenOutput}</React.Fragment>);
                        }
                    } else {
                        const elseNode = props.else || (elseChildren.length > 0 ? elseChildren[elseChildren.length - 1] : null);
                        if (elseNode) {
                            hasMatched = true;
                            const elseOutput = processChildren(elseNode);
                            if (elseOutput !== null) {
                                output.push(<React.Fragment key={`Else-${index}`}>{elseOutput}</React.Fragment>);
                            }
                        }
                    }
                    break;
                }

                case 'Condition.Not': {
                    const conditionResult = !evaluateTest(props.test, contextValue);
                    if (conditionResult) {
                        const notOutput = processChildren(props.children);
                        if (notOutput !== null) {
                            output.push(<React.Fragment key={`Not-${index}`}>{notOutput}</React.Fragment>);
                        }
                    }
                    break;
                }

                case 'Condition.Switch': {
                    const switchValue = props.value !== undefined ? props.value : contextValue;
                    const switchOutput = processSwitch(props, switchValue);
                    if (switchOutput !== null) {
                        output.push(<React.Fragment key={`Switch-${index}`}>{switchOutput}</React.Fragment>);
                    }
                    break;
                }

                case 'Condition.ThrowIf': {
                    const conditionResult = evaluateTest(props.test, contextValue);
                    if (conditionResult) {
                        props.throwHandler();
                    }
                    break;
                }

                default:
                    const inner = processChildren(props.children)

                    // 如果内部有 Condition.* 被解析到，返回处理后的 clone；
                    // 否则保持原状
                    output.push(
                      inner != null
                        ? cloneElement(child, { ...props, children: inner })
                        : child
                    )
                    break
            }
        });

        return output.length
          ? <>{React.Children.toArray(output)}</>
          : null
    };

    const separateWhenElseChildren = (children: ReactNode): { whenChildren: ReactNode[]; elseChildren: ReactNode[]; elseCount: number } => {
        const whenChildren: ReactNode[] = [];
        const elseChildren: ReactNode[] = [];
        let elseCount = 0;

        React.Children.forEach(children, (child, index) => {
            if (!isValidElement(child)) {
                whenChildren.push(child);
                return;
            }

            const { type, props } = child;
            const displayName = (type as any).displayName;

            if (displayName === 'Condition.Else') {
                elseCount++;
                elseChildren.push(
                    <React.Fragment key={`else-${index}`}>
                        {props.children}
                    </React.Fragment>
                );
            } else {
                whenChildren.push(child);
            }
        });

        return { whenChildren, elseChildren, elseCount };
    };

    const evaluateTest = (test: boolean | ((value: T) => boolean), value?: T): boolean => {
        if (typeof test === 'boolean') {
            return test;
        } else if (typeof test === 'function') {
            if (value === undefined) {
                throw new Error('No value provided to Conditional, but test function requires value.');
            }
            return test(value);
        }
        return false;
    };

    const processSwitch = (props: ConditionSwitchProps<T>, switchValue?: T): ReactNode => {
        let switchMatched = false;
        const switchOutput: ReactNode[] = [];

        React.Children.forEach(props.children, (child, index) => {
            if (!isValidElement(child)) {
                return;
            }

            const { type, props: childProps } = child;
            const displayName = (type as any).displayName;

            if (displayName === 'Condition.Case') {
                if (switchMatched && childProps.break !== false) return;

                const caseMatch = evaluateCase(childProps.case, switchValue);
                if (caseMatch) {
                    switchMatched = true;
                    switchOutput.push(
                        <ConditionalContext.Provider value={switchValue} key={`Switch-Case-${index}`}>
                            {childProps.children}
                        </ConditionalContext.Provider>
                    );
                }
            } else if (displayName === 'Condition.Default') {
                if (!switchMatched) {
                    switchOutput.push(
                        <ConditionalContext.Provider value={switchValue} key={`Switch-Default-${index}`}>
                            {childProps.children}
                        </ConditionalContext.Provider>
                    );
                }
            } else {
                console.error('Condition.Case and Condition.Default must be used within Condition.Switch');
            }
        });

        return switchOutput.length > 0 ? <>{switchOutput}</> : null;
    };

    const evaluateCase = (caseValue: T | ((value: T) => boolean), switchValue?: T): boolean => {
        if (typeof caseValue === 'function') {
            if (switchValue === undefined) {
                throw new Error('No value provided to Condition.Switch, but case function requires value.');
            }
            return caseValue(switchValue);
        } else {
            return switchValue === caseValue;
        }
    };

    return (
        <ConditionalContext.Provider value={contextValue}>
            {processChildren(children)}
        </ConditionalContext.Provider>
    );
};

export { Conditional, Condition };
