import pandas as pd

# 设置pandas显示选项
pd.set_option('display.max_columns', None)
pd.set_option('display.max_rows', None)
pd.set_option('display.width', None)
pd.set_option('display.max_colwidth', None)

# 读取Excel文件
df = pd.read_excel('/Users/mk/Workspaces/Huayuan/smart-energy-hub/zhibiao.xlsx')

# 打印数据形状
print(f"数据形状: {df.shape}")

# 打印列名
print("\n列名:")
print(df.columns.tolist())

# 打印前几行来了解数据结构
print("\n前10行数据:")
print(df.head(10))

# 将数据保存到文件
with open('excel_data.txt', 'w', encoding='utf-8') as f:
    f.write(f"数据形状: {df.shape}\n\n")
    f.write(f"列名: {df.columns.tolist()}\n\n")
    
    # 写入所有非空数据
    f.write("所有数据:\n")
    for i, row in df.iterrows():
        if not row.isna().all():  # 只写入非全空行
            f.write(f"行{i}: {row.to_dict()}\n")

print("数据已保存到 excel_data.txt 文件中")